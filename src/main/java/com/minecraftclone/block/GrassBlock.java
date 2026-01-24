package com.minecraftclone.block;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;

public class GrassBlock extends Block {

    @Override
    public Geometry createGeometry(AssetManager assetManager) {
        Geometry geom = new Geometry("Stone", MeshLibrary.CUBE);

        geom.setMaterial(getMaterial(geom, assetManager, "textures/blocks/stone.png"));
        return geom;
    }
}
