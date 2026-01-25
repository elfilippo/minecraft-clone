package com.minecraftclone.block;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;

class DefaultBlock extends Block {

    private String blockTexture;

    public DefaultBlock(String blockTexture) {
        this.blockTexture = blockTexture;
    }

    @Override
    public Geometry createGeometry(AssetManager assetManager) {
        Geometry geom = new Geometry("Stone", MeshLibrary.CUBE);

        geom.setMaterial(getMaterial(geom, assetManager, "textures/blocks/" + blockTexture));
        return geom;
    }
}
