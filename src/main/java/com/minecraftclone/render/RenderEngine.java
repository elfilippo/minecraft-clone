package com.minecraftclone.render;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.minecraftclone.block.Blocks;
import com.minecraftclone.world.World;

public class RenderEngine {

    private final World world;
    private final AssetManager assetManager;
    private final Node rootNode;

    public RenderEngine(Node rootNode, AssetManager assetManager, BulletAppState bulletAppState) {
        // world owns chunks + collision
        world = new World(rootNode, bulletAppState.getPhysicsSpace(), assetManager);
        this.rootNode = rootNode;
        this.assetManager = assetManager;

        // test terrain
        testMap();
    }

    private void testMap() {
        for (int x = 0; x < 10; x++) {
            for (int z = 0; z < 10; z++) {
                if (Math.random() * 3 < 1) {
                    world.placeBlock(x, 0, z, Blocks.DIAMOND_BLOCK);
                    continue;
                }
                if (Math.random() * 2 < 1) {
                    world.placeBlock(x, 0, z, Blocks.GOLD_BLOCK);
                    continue;
                }
                world.placeBlock(x, 0, z, Blocks.EMERALD_BLOCK);
            }
        }

        world.placeBlock(1, 1, 0, Blocks.STONE);
        world.placeBlock(1, 2, 0, Blocks.STONE);
        world.placeBlock(1, 3, 0, Blocks.STONE);
        world.placeBlock(2, 1, 0, Blocks.STONE);
        world.placeBlock(0, 1, 0, Blocks.STONE);

        // Render the chunk at a specific location for testing
    }

    public World getWorld() {
        return world;
    }
}
