package com.minecraftclone.render;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import java.util.HashMap;
import java.util.Map;

public final class BlockMaterialCache {

    private static final Map<String, Material> CACHE = new HashMap<>();

    /**
     * creates material for a block by loading png texture and using unshaded material if not already loaded
     * @param texture name of the texture in resources/textures/block without type extension
     * @param assetManager
     * @return
     */
    public static Material get(String texture, AssetManager assetManager) {
        //NOTE: cache clearing to be added

        //DOES: take material for the texture and name out of map or create it if absent
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
