package com.minecraftclone.world.chunks;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.minecraftclone.block.Block;
import com.minecraftclone.world.World;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkManager {

    //NOTE: needs to add setting for less than one chunk per tick to minimize lag when loading world
    private static final int CHUNKS_PER_TICK = 1;

    //IS: how many completed chunk results are applied to the scene per frame
    private static final int APPLY_PER_FRAME = 2;

    private final SimpleApplication app;
    private final World world;
    private final PhysicsSpace physicsSpace;
    private final int renderDistance;
    private final int simulationDistance;

    //IS: array double-ended queue
    //INFO: allows efficient adding & removing from both ends
    private final Queue<ChunkPos> queue = new ArrayDeque<>();

    //IS: set of chunk positions
    //INFO: set is used for super-fast lookup time (check if chunk is already queued)
    private final Set<ChunkPos> queued = new HashSet<>();

    //IS: set of loaded chunks and chunks with collision
    private final Set<ChunkPos> loaded = new HashSet<>();
    private final Set<ChunkPos> hasCollision = new HashSet<>();

    //IS: thread pool for background chunk building
    //INFO: uses one less thread than available to leave main thread free
    private final ExecutorService executor = Executors.newFixedThreadPool(
        Math.max(1, Runtime.getRuntime().availableProcessors() - 1)
    );

    //IS: completed chunk build results waiting to be applied to the scene graph
    //INFO: background threads write, main thread reads — must be concurrent
    private final Queue<ChunkBuildResult> readyToApply = new ConcurrentLinkedQueue<>();

    //IS: chunks currently being built on a background thread
    //INFO: checked by enqueueMissing() to avoid submitting the same chunk twice
    private final Set<ChunkPos> inProgress = new HashSet<>();

    private Vector3f playerPos;

    //IS: chunk player is/was in
    private int chunkX, lastChunkX;
    private int chunkZ, lastChunkZ;

    private boolean reloadChunks = true;

    /**
     * handles chunk generation, loading, unloading, and everything else
     * <p>
     */
    public ChunkManager(SimpleApplication app, World world, int renderDistance, int simulationDistance) {
        this.app = app;
        this.world = world;
        this.renderDistance = renderDistance;
        this.simulationDistance = simulationDistance;

        BulletAppState bullet = app.getStateManager().getState(BulletAppState.class);

        this.physicsSpace = bullet.getPhysicsSpace();
    }

    /**
     * updates chunk queue
     * @param playerPos
     */
    public void update(Vector3f playerPos) {
        this.playerPos = playerPos;

        lastChunkX = chunkX;
        lastChunkZ = chunkZ;

        chunkX = Math.floorDiv((int) playerPos.x, Chunk.SIZE);
        chunkZ = Math.floorDiv((int) playerPos.z, Chunk.SIZE);

        if (chunkX != lastChunkX || chunkZ != lastChunkZ || reloadChunks) {
            //INFO: called every frame
            enqueueMissing();

            //DOES: unload chunks outside render distance
            unloadChunks();
            removeCollision();
            reloadChunks = false;
        }

        //DOES: submit chunk build tasks to background thread
        processQueue();

        //DOES: apply completed chunk results to scene graph on main thread
        applyReadyChunks();
    }

    /**
     * adds missing chunks in player's render distance to queue,
     * chunks closer to player are enqueued first
     */
    private void enqueueMissing() {
        //DOES: get chunk coords from player pos
        //INFO: floorDiv is used to get the correct coords even with negative playerPos
        int chunkX = Math.floorDiv((int) playerPos.x, Chunk.SIZE);
        int chunkZ = Math.floorDiv((int) playerPos.z, Chunk.SIZE);

        //DOES: loop through each ring of distance from player
        //INFO: spiral / diamond order (like mc)
        for (int distance = 0; distance <= renderDistance; distance++) {
            //DOES: iterate from -distance to +distance in each upper loop (go left to right across layer)
            //NOTE: ex. for ring (distance) 2 -> -2, -1, 0, 1, 2
            for (int offsetX = -distance; offsetX <= distance; offsetX++) {
                //DOES: check how far vertically can be moved for each offsetX in diamond shape
                /* //INFO: visual:
                distanceZ1: 0  1  2  1  0
                                  x
                               x  x  x
                            x  x  x  x  x
                               x  x  x
                                  x
                offsetX:   -2 -1  0  1  2  */
                //IS: distance - absolute value of offsetX (how far up chunks need to be rendered from player)
                int distanceZ1 = distance - Math.abs(offsetX);

                //IS: inverted distanceZ1 (how far down chunks need to be rendered from player)
                int distanceZ2 = -distanceZ1;

                //DOES: add chunks above player to queue if not already queued
                //INFO: offset needs to be added to chunk coords since it's relative to player
                addIfMissing(chunkX + offsetX, chunkZ + distanceZ1);

                //DOES: add chunks below to queue if not same chunk as already added above
                if (distanceZ1 != distanceZ2) addIfMissing(chunkX + offsetX, chunkZ + distanceZ2);
            }
        }
    }

    /**
     * checks if chunk at coordinates exists & adds to queue if not
     * @param chunkX
     * @param chunkZ
     */
    private void addIfMissing(int chunkX, int chunkZ) {
        //NOTE: needs to add chunks at different y coordinates as well later

        ChunkPos pos = new ChunkPos(chunkX, 0, chunkZ);

        //CASE: if loaded, in progress, or queued
        if (loaded.contains(pos) || queued.contains(pos) || inProgress.contains(pos)) return;

        //DOES: add to queue and queue set
        queue.add(pos);
        queued.add(pos);
    }

    /**
     * submits chunk build tasks to the background thread executor
     */
    private void processQueue() {
        //NOTE: needs to support fractional chunks per tick (setting?)

        //DOES: loop for however many chunks can be submitted per tick
        for (int i = 0; i < CHUNKS_PER_TICK; i++) {
            //DOES: take first element in queue out
            ChunkPos pos = queue.poll();

            //CASE: no elements in queue
            if (pos == null) return;

            //DOES: remove from queue set and mark as in progress
            queued.remove(pos);
            inProgress.add(pos);

            //DOES: snapshot neighbor arrays on main thread before submitting to avoid data races
            //INFO: background thread must not read from world directly
            Block[][][] neighborUp = getNeighborBlocks(pos.x, pos.y + 1, pos.z);
            Block[][][] neighborDown = getNeighborBlocks(pos.x, pos.y - 1, pos.z);
            Block[][][] neighborNorth = getNeighborBlocks(pos.x, pos.y, pos.z + 1);
            Block[][][] neighborSouth = getNeighborBlocks(pos.x, pos.y, pos.z - 1);
            Block[][][] neighborEast = getNeighborBlocks(pos.x + 1, pos.y, pos.z);
            Block[][][] neighborWest = getNeighborBlocks(pos.x - 1, pos.y, pos.z);

            //DOES: create chunk object on main thread (not yet attached to scene)
            Chunk chunk = new Chunk(world, pos.x, pos.y, pos.z, app.getAssetManager(), physicsSpace);
            boolean buildCollision = inDistance(pos, simulationDistance);

            ChunkBuildTask task = new ChunkBuildTask(
                pos,
                chunk,
                neighborUp,
                neighborDown,
                neighborNorth,
                neighborSouth,
                neighborEast,
                neighborWest,
                buildCollision,
                true
            );

            //DOES: submit task to background thread, add result to readyToApply when done
            executor.submit(() -> {
                try {
                    ChunkBuildResult result = task.call();
                    readyToApply.add(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * gets blocks array of neighboring chunk at given chunk coords
     * @param cx
     * @param cy
     * @param cz
     * @return blocks array, null if chunk not loaded
     */
    private Block[][][] getNeighborBlocks(int cx, int cy, int cz) {
        Chunk chunk = world.getChunk(new ChunkPos(cx, cy, cz));
        return chunk != null ? chunk.getBlocks() : null;
    }

    /**
     * applies completed chunk build results to the scene graph on the main thread
     * limited to APPLY_PER_FRAME results per frame to avoid main thread spikes
     */
    private void applyReadyChunks() {
        for (int i = 0; i < APPLY_PER_FRAME; i++) {
            ChunkBuildResult result = readyToApply.poll();
            if (result == null) return;

            //CASE: chunk was unloaded while task was running, discard result
            if (!inProgress.contains(result.pos)) continue;

            inProgress.remove(result.pos);
            loaded.add(result.pos);

            //DOES: scene graph touches — safe since we are on the main thread
            if (!world.hasChunk(result.pos)) {
                world.addChunk(result.chunk);
                app.getRootNode().attachChild(result.chunk.getNode());
            }
            result.chunk.applyMesh(result.meshes);

            //DOES: add collision body to physics space if it was built
            if (result.collisionShape != null) {
                RigidBodyControl body = new RigidBodyControl(result.collisionShape, 0f);
                result.chunk.getNode().addControl(body);
                physicsSpace.add(body);
                hasCollision.add(result.pos);
            }

            //DOES: rebuild neighbors to cull faces across chunk borders properly
            //INFO: not recursive, since rebuildNeighbor does not rebuild neighbors for itself (does not loop infinitely)
            requeueNeighborRebuild(result.pos.x + 1, result.pos.y, result.pos.z);
            requeueNeighborRebuild(result.pos.x - 1, result.pos.y, result.pos.z);
            requeueNeighborRebuild(result.pos.x, result.pos.y + 1, result.pos.z);
            requeueNeighborRebuild(result.pos.x, result.pos.y - 1, result.pos.z);
            requeueNeighborRebuild(result.pos.x, result.pos.y, result.pos.z + 1);
            requeueNeighborRebuild(result.pos.x, result.pos.y, result.pos.z - 1);
        }
    }

    /**
     * processes all chunks with collision and removes it if outside simulation distance
     */
    private void removeCollision() {
        Set<ChunkPos> collisionRemoved = new HashSet<>();
        for (ChunkPos pos : hasCollision) {
            if (inDistance(pos, simulationDistance)) continue;

            Chunk chunk = world.getChunk(pos);
            if (chunk == null) continue;
            chunk.removeCollision();
            collisionRemoved.add(pos);
        }
        hasCollision.removeAll(collisionRemoved);
    }

    private void unloadChunks() {
        Set<ChunkPos> unloaded = new HashSet<>();
        for (ChunkPos pos : loaded) {
            if (inDistance(pos, renderDistance)) continue;

            Chunk chunk = world.getChunk(pos);
            if (chunk == null) continue;
            chunk.unload();
            unloaded.add(pos);
        }
        loaded.removeAll(unloaded);
    }

    /**
     * requeues a loaded neighbor chunk for a mesh-only background rebuild
     * used when a new chunk loads next to an existing one to update border face culling
     * does not regenerate terrain, only rebuilds the mesh
     * @param cx
     * @param cy
     * @param cz
     */
    private void requeueNeighborRebuild(int cx, int cy, int cz) {
        ChunkPos pos = new ChunkPos(cx, cy, cz);
        Chunk neighbor = world.getChunk(pos);

        //CASE: neighbor doesn't exist or is already being rebuilt
        if (neighbor == null || !loaded.contains(pos) || inProgress.contains(pos) || queued.contains(pos)) return;

        //DOES: snapshot neighbor's own neighbors for the mesh rebuild
        Block[][][] neighborUp = getNeighborBlocks(cx, cy + 1, cz);
        Block[][][] neighborDown = getNeighborBlocks(cx, cy - 1, cz);
        Block[][][] neighborNorth = getNeighborBlocks(cx, cy, cz + 1);
        Block[][][] neighborSouth = getNeighborBlocks(cx, cy, cz - 1);
        Block[][][] neighborEast = getNeighborBlocks(cx + 1, cy, cz);
        Block[][][] neighborWest = getNeighborBlocks(cx - 1, cy, cz);

        //DOES: mark in progress so it won't be queued again
        loaded.remove(pos);
        inProgress.add(pos);

        ChunkBuildTask task = new ChunkBuildTask(
            pos,
            neighbor,
            neighborUp,
            neighborDown,
            neighborNorth,
            neighborSouth,
            neighborEast,
            neighborWest,
            hasCollision.contains(pos), // preserve collision state
            false // no terrain generation
        );

        //DOES: submit mesh-only rebuild to background thread
        executor.submit(() -> {
            try {
                ChunkBuildResult result = task.call();
                readyToApply.add(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * checks if chunk is in distance
     * @param pos
     * @param distance
     * @return
     */
    private boolean inDistance(ChunkPos pos, int distance) {
        int chunkX = Math.floorDiv((int) playerPos.x, Chunk.SIZE);
        int chunkZ = Math.floorDiv((int) playerPos.z, Chunk.SIZE);
        return Math.abs(pos.x - chunkX) + Math.abs(pos.z - chunkZ) <= distance;
    }

    /**
     * adds to hasCollision set so that collision can be scheduled to be removed
     * @param pos
     */
    public void addToHasCollision(ChunkPos pos) {
        hasCollision.add(pos);
    }

    /**
     * returns if chunk at given pos has collision
     * @param pos
     * @return
     */
    public boolean hasCollision(ChunkPos pos) {
        return hasCollision.contains(pos);
    }

    /**
     * shuts down the background thread executor
     * called when the game exits to prevent threads from keeping the JVM alive
     */
    public void shutdown() {
        executor.shutdownNow();
    }
}
