package com.minecraftclone;

import com.jme3.app.SimpleApplication;
import com.minecraftclone.block.StoneBlock;
import com.minecraftclone.world.World;

/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        World world = new World(rootNode, assetManager);
        world.placeBlock(0, 0, 0, new StoneBlock());
        world.placeBlock(1, 0, 0, new StoneBlock());
        world.placeBlock(2, 0, 0, new StoneBlock());
        world.placeBlock(1, 1, 0, new StoneBlock());
        world.placeBlock(1, 2, 0, new StoneBlock());
    }
}
