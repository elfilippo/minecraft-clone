package com.minecraftclone.world.chunks;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.minecraftclone.world.World;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ChunkManager {

    //NOTE: needs to add setting for less than one chunk per tick to minimize lag when loading world
    private static final int CHUNKS_PER_TICK = 1;

    private final SimpleApplication app;
    private final World world;
    private final PhysicsSpace physicsSpace;
    private final int renderDistance;
    private final int simulationDistance;

    //IS: array double-ended queue
    //INFO: allows efficient adding & removing from both ends
    private final Queue<ChunkPos> queue = new ArrayDeque<>();
    private final Queue<ChunkPos> collisionQueue = new ArrayDeque<>();

    //IS: set of chunk positions
    //INFO: set is used for super-fast lookup time (check if chunk is already queued)
    private final Set<ChunkPos> queued = new HashSet<>();
    private final Set<ChunkPos> collisionQueued = new HashSet<>();

    //IS: set of loaded chunks and chunks with collision
    private final Set<ChunkPos> loaded = new HashSet<>();
    private final Set<ChunkPos> hasCollision = new HashSet<>();

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

        //DOES: generate, render & add collision to chunks in queue
        processQueue();
        processCollisionQueue();
    }

    /**
     * adds missing chunks in player's render distance to queue,
     * chunks closer to player are enqueued first
     * @param playerPos
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

                //DOES: add collision to queue if within simulation distance
                if (distance <= simulationDistance) {
                    addCollisionIfMissing(chunkX + offsetX, chunkZ + distanceZ1);
                    if (distanceZ1 != distanceZ2) addCollisionIfMissing(chunkX + offsetX, chunkZ + distanceZ2);
                }
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

        //CASE: if loaded or queued
        if (loaded.contains(pos) || queued.contains(pos)) return;

        //DOES: add to queue and queue set
        queue.add(pos);
        queued.add(pos);
    }

    /**
     * adds given chunk to collision queue if not already there
     * @param chunkX
     * @param chunkZ
     */
    private void addCollisionIfMissing(int chunkX, int chunkZ) {
        ChunkPos pos = new ChunkPos(chunkX, 0, chunkZ);
        if (!loaded.contains(pos)) return;
        if (hasCollision.contains(pos) || collisionQueued.contains(pos)) return;

        collisionQueue.add(pos);
        collisionQueued.add(pos);
    }

    /**
     * loads or generates chunks in queue
     */
    private void processQueue() {
        //NOTE: needs to support fractional chunks per tick (setting?)

        //DOES: loop for however many chunks can be generated per tick
        for (int i = 0; i < CHUNKS_PER_TICK; i++) {
            //DOES: take first element in queue out
            ChunkPos pos = queue.poll();

            //CASE: no elements in queue
            if (pos == null) return;

            //DOES: remove from queue set
            queued.remove(pos);

            //DOES: load or generate chunk
            loadChunk(pos);
            loaded.add(pos);
        }
    }

    private void processCollisionQueue() {
        ChunkPos pos = collisionQueue.poll();
        if (pos == null) return;
        collisionQueued.remove(pos);

        Chunk chunk = world.getChunk(pos);
        if (chunk == null) return;
        chunk.addCollision();
        hasCollision.add(pos);
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

    /**
     * loads or generates chunk at position
     * @param pos
     */
    private void loadChunk(ChunkPos pos) {
        //CASE: if exists but unloaded
        if (world.hasChunk(pos)) {
            Chunk chunk = world.getChunk(pos);
            chunk.setDirty(true);
            world.getChunk(pos).rebuild();
            return;
        }

        Chunk chunk = new Chunk(pos.x, pos.y, pos.z, app.getAssetManager(), physicsSpace);

        //DOES: add chunk to map
        world.addChunk(chunk);

        //DOES: add chunk to root node
        app.getRootNode().attachChild(chunk.getNode());

        //DOES: generate chunk terrain
        TerrainGenerator.generateChunk(chunk);

        //DOES: rebuild chunk with new terrain
        chunk.rebuild();

        //DOES: add collision right away if in simulation distance
        //INFO: enqueueMissing() skips if already has collision, so it only gets added once
        if (inDistance(pos, simulationDistance)) {
            chunk.addCollision();
            hasCollision.add(pos);
        }
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
}
