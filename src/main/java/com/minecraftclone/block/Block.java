package com.minecraftclone.block;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;

public abstract class Block {

    protected int x, y, z;

    public abstract Geometry createGeometry(AssetManager assetManager);
}
