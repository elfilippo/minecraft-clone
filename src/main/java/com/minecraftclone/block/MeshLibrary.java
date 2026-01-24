package com.minecraftclone.block;

import com.jme3.scene.shape.Box;

public class MeshLibrary {

    public static final Box CUBE;

    static {
        CUBE = new Box(0.5f, 0.5f, 0.5f);
    }
}
