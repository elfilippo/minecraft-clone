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

    //IS: array double-ended queue
    //INFO: allows efficient adding & removing from both ends
    private final Queue<ChunkPos> queue = new ArrayDeque<>();

    //IS: set of chunk positions
    //INFO: set is used for super-fast lookup time (check if chunk is already queued)
    private final Set<ChunkPos> queued = new HashSet<>();

    public ChunkManager(SimpleApplication app, World world, int renderDistance) {
        this.app = app;
        this.world = world;
        this.renderDistance = renderDistance;

        BulletAppState bullet = app.getStateManager().getState(BulletAppState.class);

        this.physicsSpace = bullet.getPhysicsSpace();
    }

    /**
     * updates chunk queue
     * @param playerWorldPos
     */
    public void update(Vector3f playerWorldPos) {
        //INFO: called every frame
        enqueueMissing(playerWorldPos);

        //DOES: generate & render chunks in queue
        processQueue();
    }

    /**
     * adds missing chunks in player's render distance to queue,
     * chunks closer to player are enqueued first
     * @param playerPos
     */
    private void enqueueMissing(Vector3f playerPos) {
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

        //CASE: if exists or queued
        if (world.hasChunk(pos) || queued.contains(pos)) return;

        //DOES: add to queue and queue set
        queue.add(pos);
        queued.add(pos);
    }

    /**
     * generates chunks in queue
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

            //DOES: generate chunk
            generateChunk(pos);
        }
    }

    /**
     * generates chunk at position
     * @param pos
     */
    private void generateChunk(ChunkPos pos) {
        Chunk chunk = new Chunk(pos.x, pos.y, pos.z, app.getAssetManager());

        //DOES: add chunk to map
        world.addChunk(chunk);

        //DOES: add chunk to root node
        app.getRootNode().attachChild(chunk.getNode());

        //DOES: generate chunk terrain
        TerrainGenerator.generateChunk(chunk);

        //DOES: rebuild chunk with new terrain
        chunk.rebuild(physicsSpace);
    }
}
