package com.minecraftclone.render;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;

/**
 * kept for API compatibility.
 * now delegates to BlockAtlas — all block faces share one atlas material.
 * the texture parameter is ignored since the atlas handles all textures.
 */
public final class BlockMaterialCache {

    /**
     * returns the shared atlas material.
     * the texture name parameter is no longer used — UV offsets into the atlas
     * are baked into each mesh's vertex data by ChunkMeshBuilder.
     * @param texture ignored, kept for compatibility
     * @param assetManager ignored, kept for compatibility
     * @return the shared atlas material
     */
    public static Material get(String texture, AssetManager assetManager) {
        return BlockAtlas.getMaterial();
    }

    private BlockMaterialCache() {}
}
