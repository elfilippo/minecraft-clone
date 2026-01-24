package com.minecraftclone.render;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.block.Blocks;
import com.minecraftclone.entitiy.PlayerCharacter;
import com.minecraftclone.world.ActionInput;
import com.minecraftclone.world.World;

public class RenderEngine {

    private World world;

    public RenderEngine(Node rootNode, AssetManager assetManager, BulletAppState bulletAppState) {
        //TODO: move world to main
        //TODO: separate block creation in memory (Chunk) from visual block creation (RenderEngine)
        world = new World(rootNode, assetManager, bulletAppState);
        testMap(world, bulletAppState);
    }

    private void testMap(World world, BulletAppState bulletAppState) {
        world.placeBlock(0, 1, 0, Blocks.DIAMOND_BLOCK);
        world.addCollision(bulletAppState);
        world.placeBlock(1, 1, 0, Blocks.DIAMOND_BLOCK);
        world.addCollision(bulletAppState);
        world.placeBlock(2, 1, 0, Blocks.DIAMOND_BLOCK);
        world.addCollision(bulletAppState);
        world.placeBlock(1, 2, 0, Blocks.DIAMOND_BLOCK);
        world.addCollision(bulletAppState);
        world.placeBlock(1, 3, 0, Blocks.DIAMOND_BLOCK);
        world.addCollision(bulletAppState);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                world.placeBlock(i, 0, j, Blocks.DIRT);
                world.addCollision(bulletAppState);
            }
        }
    }

    public PlayerCharacter getPlayerCharacter() {
        return world.getPlayerCharacter();
    }

    public void tick(ActionInput input, Camera cam) {
        world.tick(input, cam);
    }
}
