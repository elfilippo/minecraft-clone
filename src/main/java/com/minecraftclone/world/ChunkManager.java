package com.minecraftclone.world;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ChunkManager {

    private static final int CHUNKS_PER_TICK = 1;

    private final SimpleApplication app;
    private final World world;
    private final PhysicsSpace physicsSpace;
    private final int renderDistance;

    private final Queue<ChunkPos> queue = new ArrayDeque<>();
    private final Set<ChunkPos> queued = new HashSet<>();

    public ChunkManager(SimpleApplication app, World world, int renderDistance) {
        this.app = app;
        this.world = world;
        this.renderDistance = renderDistance;

        BulletAppState bullet = app.getStateManager().getState(BulletAppState.class);

        this.physicsSpace = bullet.getPhysicsSpace();
    }

    // called once per frame
    public void update(Vector3f playerWorldPos) {
        enqueueMissing(playerWorldPos);
        processQueue();
    }

    // ------------------------------------------------------

    private void enqueueMissing(Vector3f playerPos) {
        int cx = Math.floorDiv((int) playerPos.x, Chunk.SIZE);
        int cz = Math.floorDiv((int) playerPos.z, Chunk.SIZE);

        // Spiral / diamond order
        for (int d = 0; d <= renderDistance; d++) {
            // distance from player chunk
            for (int dx = -d; dx <= d; dx++) {
                int dz1 = d - Math.abs(dx);
                int dz2 = -dz1;

                addIfMissing(cx + dx, cz + dz1);
                if (dz1 != dz2) addIfMissing(cx + dx, cz + dz2);
            }
        }
    }

    private void addIfMissing(int x, int z) {
        ChunkPos pos = new ChunkPos(x, 0, z);
        if (world.hasChunk(pos) || queued.contains(pos)) return;

        queue.add(pos);
        queued.add(pos);
    }

    private void processQueue() {
        for (int i = 0; i < CHUNKS_PER_TICK; i++) {
            ChunkPos pos = queue.poll();
            if (pos == null) return;

            queued.remove(pos);
            generateChunk(pos);
        }
    }

    // ------------------------------------------------------

    private void generateChunk(ChunkPos pos) {
        Chunk chunk = new Chunk(pos.x, pos.y, pos.z, app.getAssetManager());

        world.addChunk(chunk);

        app.getRootNode().attachChild(chunk.getNode());

        TerrainGenerator.generateChunk(chunk);

        chunk.rebuild(physicsSpace);
    }
}
