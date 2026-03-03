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
import java.util.concurrent.ConcurrentHashMap;
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

    //IS: queue of chunk positions waiting to be submitted for building
    //INFO: ArrayDeque allows efficient adding and removing from both ends
    private final Queue<ChunkPos> queue = new ArrayDeque<>();

    //IS: set for fast lookup of what's already in the queue
    private final Set<ChunkPos> queued = new HashSet<>();

    //IS: set of chunk positions that are fully loaded and visible
    private final Set<ChunkPos> loaded = new HashSet<>();

    //IS: set of chunk positions that have collision bodies in the physics space
    private final Set<ChunkPos> hasCollision = new HashSet<>();

    //IS: thread pool for background chunk building
    //INFO: uses one less thread than available to leave main thread responsive
    private final ExecutorService executor = Executors.newFixedThreadPool(
        Math.max(1, Runtime.getRuntime().availableProcessors() - 1)
    );

    //IS: completed chunk build results waiting to be applied to the scene graph on the main thread
    //INFO: background threads write, main thread reads — must be concurrent
    private final Queue<ChunkBuildResult> readyToApply = new ConcurrentLinkedQueue<>();

    //IS: chunks currently being built on a background thread
    //INFO: ConcurrentHashMap.newKeySet() gives a thread-safe HashSet
    private final Set<ChunkPos> inProgress = ConcurrentHashMap.newKeySet();

    //IS: chunks that were rebuilt synchronously on the main thread due to block placement/breaking
    //INFO: stale background results for these chunks are discarded in applyReadyChunks
    private final Set<ChunkPos> manuallyRebuilt = new HashSet<>();

    private Vector3f playerPos;

    //IS: current and previous chunk the player is in (used to detect chunk crossings)
    private int chunkX, lastChunkX;
    private int chunkZ, lastChunkZ;

    private boolean reloadChunks = true;

    /**
     * handles chunk generation, loading, unloading, and background mesh building
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
     * called every frame — updates the chunk queue, submits tasks, and applies results
     * @param playerPos current player physics location
     */
    public void update(Vector3f playerPos) {
        this.playerPos = playerPos;

        lastChunkX = chunkX;
        lastChunkZ = chunkZ;

        chunkX = Math.floorDiv((int) playerPos.x, Chunk.SIZE);
        chunkZ = Math.floorDiv((int) playerPos.z, Chunk.SIZE);

        if (chunkX != lastChunkX || chunkZ != lastChunkZ || reloadChunks) {
            //DOES: queue any chunks in render distance that aren't loaded yet
            enqueueMissing();

            //DOES: unload chunks outside render distance
            unloadChunks();
            removeCollision();
            reloadChunks = false;
        }

        //DOES: submit chunk build tasks to background thread pool
        processQueue();

        //DOES: apply completed background results to scene graph on main thread
        applyReadyChunks();
    }

    /**
     * adds all missing chunks within render distance to the queue,
     * in diamond (closest-first) order matching Minecraft's load order
     */
    private void enqueueMissing() {
        int chunkX = Math.floorDiv((int) playerPos.x, Chunk.SIZE);
        int chunkZ = Math.floorDiv((int) playerPos.z, Chunk.SIZE);

        //DOES: loop through each diamond ring of distance from player
        for (int distance = 0; distance <= renderDistance; distance++) {
            /* INFO: diamond pattern visual for distance=2:
               distanceZ1: 0  1  2  1  0
                                 x
                              x  x  x
                           x  x  x  x  x
                              x  x  x
                                 x
               offsetX:   -2 -1  0  1  2  */
            for (int offsetX = -distance; offsetX <= distance; offsetX++) {
                int distanceZ1 = distance - Math.abs(offsetX);
                int distanceZ2 = -distanceZ1;

                addIfMissing(chunkX + offsetX, chunkZ + distanceZ1);
                if (distanceZ1 != distanceZ2) addIfMissing(chunkX + offsetX, chunkZ + distanceZ2);
            }
        }
    }

    /**
     * adds a chunk to the queue if it isn't already loaded, queued, or in progress
     * @param chunkX chunk x coordinate
     * @param chunkZ chunk z coordinate
     */
    private void addIfMissing(int chunkX, int chunkZ) {
        //NOTE: needs to add chunks at different y coordinates as well later
        ChunkPos pos = new ChunkPos(chunkX, 0, chunkZ);

        if (loaded.contains(pos) || queued.contains(pos) || inProgress.contains(pos)) return;

        queue.add(pos);
        queued.add(pos);
    }

    /**
     * dequeues up to CHUNKS_PER_TICK chunks and submits them as background build tasks
     */
    private void processQueue() {
        for (int i = 0; i < CHUNKS_PER_TICK; i++) {
            ChunkPos pos = queue.poll();
            if (pos == null) return;

            queued.remove(pos);
            inProgress.add(pos);

            //DOES: snapshot neighbor blocks arrays on main thread before going off-thread
            //INFO: background thread must not read world state directly to avoid data races
            Block[][][] neighborUp    = getNeighborBlocks(pos.x, pos.y + 1, pos.z);
            Block[][][] neighborDown  = getNeighborBlocks(pos.x, pos.y - 1, pos.z);
            Block[][][] neighborNorth = getNeighborBlocks(pos.x, pos.y, pos.z + 1);
            Block[][][] neighborSouth = getNeighborBlocks(pos.x, pos.y, pos.z - 1);
            Block[][][] neighborEast  = getNeighborBlocks(pos.x + 1, pos.y, pos.z);
            Block[][][] neighborWest  = getNeighborBlocks(pos.x - 1, pos.y, pos.z);

            //DOES: reuse existing chunk if already in world (e.g. returning after unload)
            //INFO: skip terrain generation for chunks that already have block data
            boolean alreadyGenerated = world.hasChunk(pos);
            Chunk chunk = alreadyGenerated
                ? world.getChunk(pos)
                : new Chunk(world, pos.x, pos.y, pos.z, app.getAssetManager(), physicsSpace);

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
                !alreadyGenerated
            );

            //DOES: submit task to thread pool — result added to readyToApply when done
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
     * returns a deep copy of a neighbor chunk's blocks array.
     * copy is necessary to avoid data races when the main thread modifies blocks
     * while the background thread reads them for mesh building.
     * @param cx chunk x
     * @param cy chunk y
     * @param cz chunk z
     * @return copied blocks array, or null if chunk not loaded
     */
    private Block[][][] getNeighborBlocks(int cx, int cy, int cz) {
        Chunk chunk = world.getChunk(new ChunkPos(cx, cy, cz));
        if (chunk == null) return null;

        Block[][][] src = chunk.getBlocks();
        Block[][][] copy = new Block[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                System.arraycopy(src[x][y], 0, copy[x][y], 0, Chunk.SIZE);
            }
        }
        return copy;
    }

    /**
     * applies up to APPLY_PER_FRAME completed chunk results to the scene graph.
     * always runs on the main thread — safe to touch scene graph and physics space here.
     */
    private void applyReadyChunks() {
        for (int i = 0; i < APPLY_PER_FRAME; i++) {
            ChunkBuildResult result = readyToApply.poll();
            if (result == null) return;

            //CASE: chunk was unloaded while task was running — discard result
            if (!inProgress.contains(result.pos)) continue;

            inProgress.remove(result.pos);

            //CASE: chunk was manually rebuilt (block placed/broken) while task was in-flight
            //INFO: discard stale background result to avoid overwriting the correct mesh
            if (manuallyRebuilt.contains(result.pos)) {
                loaded.add(result.pos);
                continue;
            }

            loaded.add(result.pos);

            //DOES: add chunk to world if it's brand new
            if (!world.hasChunk(result.pos)) {
                world.addChunk(result.chunk);
                app.getRootNode().attachChild(result.chunk.getNode());
            }

            //DOES: apply the single merged mesh to the chunk
            result.chunk.applyMesh(result.mesh);

            //DOES: apply pre-built collision shape if this chunk is within simulation distance
            if (result.collisionShape != null) {
                result.chunk.addCollision(result.collisionShape);
                hasCollision.add(result.pos);
            }

            //DOES: trigger mesh-only rebuilds on neighbors to fix border face culling
            //INFO: not recursive — requeueNeighborRebuild never triggers further neighbor rebuilds
            requeueNeighborRebuild(result.pos.x + 1, result.pos.y, result.pos.z);
            requeueNeighborRebuild(result.pos.x - 1, result.pos.y, result.pos.z);
            requeueNeighborRebuild(result.pos.x, result.pos.y + 1, result.pos.z);
            requeueNeighborRebuild(result.pos.x, result.pos.y - 1, result.pos.z);
            requeueNeighborRebuild(result.pos.x, result.pos.y, result.pos.z + 1);
            requeueNeighborRebuild(result.pos.x, result.pos.y, result.pos.z - 1);
        }
    }

    /**
     * removes collision from all chunks that have moved outside simulation distance
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

    /**
     * unloads all chunks that have moved outside render distance
     */
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
     * submits a mesh-only background rebuild for a loaded neighbor chunk.
     * used after a new chunk loads next to it to fix cross-chunk border face culling.
     * does not regenerate terrain.
     * @param cx chunk x
     * @param cy chunk y
     * @param cz chunk z
     */
    private void requeueNeighborRebuild(int cx, int cy, int cz) {
        ChunkPos pos = new ChunkPos(cx, cy, cz);
        Chunk neighbor = world.getChunk(pos);

        //CASE: neighbor doesn't exist, already rebuilding, or manually rebuilt
        if (
            neighbor == null ||
            !loaded.contains(pos) ||
            inProgress.contains(pos) ||
            queued.contains(pos) ||
            manuallyRebuilt.contains(pos)
        ) return;

        //DOES: snapshot this neighbor's own neighbors for the mesh rebuild
        Block[][][] neighborUp    = getNeighborBlocks(cx, cy + 1, cz);
        Block[][][] neighborDown  = getNeighborBlocks(cx, cy - 1, cz);
        Block[][][] neighborNorth = getNeighborBlocks(cx, cy, cz + 1);
        Block[][][] neighborSouth = getNeighborBlocks(cx, cy, cz - 1);
        Block[][][] neighborEast  = getNeighborBlocks(cx + 1, cy, cz);
        Block[][][] neighborWest  = getNeighborBlocks(cx - 1, cy, cz);

        //DOES: move from loaded to inProgress so it won't be requeued again while building
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
            hasCollision.contains(pos), // preserve existing collision state
            false                       // no terrain generation
        );

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
     * checks if a chunk is within a given distance of the player using manhattan distance
     * @param pos chunk position
     * @param distance max manhattan distance in chunks
     * @return true if within distance
     */
    private boolean inDistance(ChunkPos pos, int distance) {
        int chunkX = Math.floorDiv((int) playerPos.x, Chunk.SIZE);
        int chunkZ = Math.floorDiv((int) playerPos.z, Chunk.SIZE);
        return Math.abs(pos.x - chunkX) + Math.abs(pos.z - chunkZ) <= distance;
    }

    /**
     * registers a chunk position as having been manually rebuilt on the main thread.
     * prevents stale in-flight background results from overwriting it.
     * @param pos chunk position
     */
    public void markManuallyRebuilt(ChunkPos pos) {
        manuallyRebuilt.add(pos);
    }

    /**
     * adds a chunk to the hasCollision set so its collision can be tracked for removal
     * @param pos chunk position
     */
    public void addToHasCollision(ChunkPos pos) {
        hasCollision.add(pos);
    }

    /**
     * returns whether a chunk has an active collision body
     * @param pos chunk position
     * @return true if chunk has collision
     */
    public boolean hasCollision(ChunkPos pos) {
        return hasCollision.contains(pos);
    }

    /**
     * shuts down the background thread pool cleanly on game exit
     */
    public void shutdown() {
        executor.shutdownNow();
    }
}
