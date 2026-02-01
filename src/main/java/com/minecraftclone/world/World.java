package com.minecraftclone.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.block.Block;
import com.minecraftclone.entitiy.EntityManager;
import com.minecraftclone.entitiy.PlayerCharacter;
import java.util.HashMap;
import java.util.Map;

public class World {

    private final Node rootNode;
    private final BulletAppState bulletAppState;
    private final AssetManager assetManager;
    private final EntityManager entityManager;

    private final Map<String, Chunk> chunks = new HashMap<>();

    public World(Node rootNode, AssetManager assetManager, BulletAppState bulletAppState, Camera cam, InputManager inputManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;

        entityManager = new EntityManager(bulletAppState, rootNode, cam, inputManager);
        entityManager.getPlayerCharacter();

        int generationDistance = 5;

        for (int x = -generationDistance; x < generationDistance; x++) {
            for (int z = -generationDistance; z < generationDistance; z++) {
                Chunk chunk = new Chunk(x, 0, z, assetManager);
                chunks.put("x,0,z", chunk);
                rootNode.attachChild(chunk.getNode());
                TerrainGenerator.generateChunk(chunk);
                chunk.rebuild(bulletAppState.getPhysicsSpace());
            }
        }
    }

    public void placeBlock(int wx, int wy, int wz, Block block) {
        int cx = Math.floorDiv(wx, Chunk.SIZE);
        int cy = Math.floorDiv(wy, Chunk.SIZE);
        int cz = Math.floorDiv(wz, Chunk.SIZE);

        int lx = Math.floorMod(wx, Chunk.SIZE);
        int ly = Math.floorMod(wy, Chunk.SIZE);
        int lz = Math.floorMod(wz, Chunk.SIZE);

        String key = cx + "," + cy + "," + cz;

        Chunk chunk = chunks.get(key);
        if (chunk == null) {
            chunk = new Chunk(cx, cy, cz, assetManager);
            chunks.put(key, chunk);
            rootNode.attachChild(chunk.getNode());
        }

        chunk.setBlock(lx, ly, lz, block);
        chunk.rebuild(bulletAppState.getPhysicsSpace());
    }

    public PlayerCharacter getPlayerCharacter() {
        return entityManager.getPlayerCharacter();
    }
}
