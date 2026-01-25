package com.minecraftclone.render;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.minecraftclone.block.Blocks;
import com.minecraftclone.world.ChunkMaterials;
import com.minecraftclone.world.World;

public class RenderEngine {

    private final World world;

    public RenderEngine(Node rootNode, AssetManager assetManager, BulletAppState bulletAppState) {
        // initialize shared chunk material
        ChunkMaterials.init(assetManager);

        // world owns chunks + collision
        world = new World(rootNode, bulletAppState.getPhysicsSpace());

        // test terrain
        testMap();
    }

    private void testMap() {
        for (int x = 0; x < 10; x++) {
            for (int z = 0; z < 10; z++) {
                world.placeBlock(x, 0, z, Blocks.DIRT);
            }
        }

        world.placeBlock(1, 1, 0, Blocks.STONE);
        world.placeBlock(1, 2, 0, Blocks.STONE);
        world.placeBlock(1, 3, 0, Blocks.STONE);
    }

    public World getWorld() {
        return world;
    }
}
