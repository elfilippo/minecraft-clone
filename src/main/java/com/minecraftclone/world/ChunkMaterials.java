package com.minecraftclone.world;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;

public final class ChunkMaterials {

    public static Material DEFAULT;

    public static void init(AssetManager assetManager) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        Texture tex = assetManager.loadTexture("textures/blocks/stone.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        mat.setTexture("ColorMap", tex);
        DEFAULT = mat;
    }

    private ChunkMaterials() {}
}
