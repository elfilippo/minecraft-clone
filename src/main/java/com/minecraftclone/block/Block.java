package com.minecraftclone.block;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;

public abstract class Block {

    private final String id;
    private final String texture;

    public Block(String id, String texture) {
        this.id = id;
        this.texture = texture;
    }

    public Geometry createGeometry(AssetManager assetManager) {
        Geometry geom = new Geometry(id, MeshLibrary.CUBE);

        geom.setMaterial(getMaterial(geom, assetManager, "textures/blocks/" + texture));
        return geom;
    }

    protected Material getMaterial(Geometry geom, AssetManager assetManager, String path) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(path);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        mat.setTexture("ColorMap", tex);
        return mat;
    }
}
