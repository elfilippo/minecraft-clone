package com.minecraftclone.render;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import java.util.HashMap;
import java.util.Map;

public final class BlockMaterialCache {

    private static final Map<String, Material> CACHE = new HashMap<>();

    public static Material get(String texture, AssetManager assetManager) {
        return CACHE.computeIfAbsent(texture, name -> {
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture tex = assetManager.loadTexture("textures/block/" + name + ".png");
            tex.setMagFilter(Texture.MagFilter.Nearest);
            tex.setMinFilter(Texture.MinFilter.Trilinear);
            mat.setTexture("ColorMap", tex);
            return mat;
        });
    }

    private BlockMaterialCache() {}
}
