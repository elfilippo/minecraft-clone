package com.minecraftclone.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.block.Block;
import com.minecraftclone.entitiy.EntityManager;
import com.minecraftclone.entitiy.PlayerCharacter;
import com.minecraftclone.input.ActionInput;
import java.util.HashMap;
import java.util.Map;

public class World {

    private final Node rootNode;
    private final BulletAppState bulletAppState;
    private final AssetManager assetManager;
    private final EntityManager entityManager;

    private final Map<String, Chunk> chunks = new HashMap<>();

    // =========================
    // CONSTRUCTOR
    // =========================

    public World(
        Node rootNode,
        AssetManager assetManager,
        BulletAppState bulletAppState,
        Camera cam,
        InputManager inputManager,
        ActionInput actionInput
    ) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;

        entityManager = new EntityManager(bulletAppState, rootNode, cam, inputManager, actionInput);
        entityManager.getPlayerCharacter();

        int generationDistance = 5;

        for (int x = -generationDistance; x <= generationDistance; x++) {
            for (int z = -generationDistance; z <= generationDistance; z++) {
                Chunk chunk = new Chunk(x, 0, z, assetManager);
                chunks.put(key(x, 0, z), chunk);
                rootNode.attachChild(chunk.getNode());

                TerrainGenerator.generateChunk(chunk);
                chunk.rebuild(bulletAppState.getPhysicsSpace());
            }
        }
    }

    // =========================
    // BLOCK ACCESS
    // =========================

    public Block getBlock(int wx, int wy, int wz) {
        Chunk c = getChunkAtWorld(wx, wy, wz);
        if (c == null) return null;

        return c.getBlock(Math.floorMod(wx, Chunk.SIZE), Math.floorMod(wy, Chunk.SIZE), Math.floorMod(wz, Chunk.SIZE));
    }

    public boolean isBlockLoaded(int wx, int wy, int wz) {
        return getChunkAtWorld(wx, wy, wz) != null;
    }

    public void setBlock(int wx, int wy, int wz, Block block) {
        int cx = Math.floorDiv(wx, Chunk.SIZE);
        int cy = Math.floorDiv(wy, Chunk.SIZE);
        int cz = Math.floorDiv(wz, Chunk.SIZE);

        int lx = Math.floorMod(wx, Chunk.SIZE);
        int ly = Math.floorMod(wy, Chunk.SIZE);
        int lz = Math.floorMod(wz, Chunk.SIZE);

        Chunk chunk = chunks.computeIfAbsent(key(cx, cy, cz), k -> {
            Chunk c = new Chunk(cx, cy, cz, assetManager);
            rootNode.attachChild(c.getNode());
            return c;
        });

        chunk.setBlock(lx, ly, lz, block);
        chunk.rebuild(bulletAppState.getPhysicsSpace());

        rebuildNeighborsIfNeeded(cx, cy, cz, lx, ly, lz);
    }

    // =========================
    // HELPERS
    // =========================

    private Chunk getChunkAtWorld(int wx, int wy, int wz) {
        int cx = Math.floorDiv(wx, Chunk.SIZE);
        int cy = Math.floorDiv(wy, Chunk.SIZE);
        int cz = Math.floorDiv(wz, Chunk.SIZE);
        return chunks.get(key(cx, cy, cz));
    }

    private void rebuildNeighborsIfNeeded(int cx, int cy, int cz, int lx, int ly, int lz) {
        if (lx == 0) rebuild(cx - 1, cy, cz);
        if (lx == Chunk.SIZE - 1) rebuild(cx + 1, cy, cz);
        if (lz == 0) rebuild(cx, cy, cz - 1);
        if (lz == Chunk.SIZE - 1) rebuild(cx, cy, cz + 1);
        if (ly == 0) rebuild(cx, cy - 1, cz);
        if (ly == Chunk.SIZE - 1) rebuild(cx, cy + 1, cz);
    }

    private void rebuild(int cx, int cy, int cz) {
        Chunk c = chunks.get(key(cx, cy, cz));
        if (c != null) {
            c.rebuild(bulletAppState.getPhysicsSpace());
        }
    }

    private static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    public PlayerCharacter getPlayerCharacter() {
        return entityManager.getPlayerCharacter();
    }
}
