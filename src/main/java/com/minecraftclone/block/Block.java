package com.minecraftclone.block;

import com.minecraftclone.block.MeshLibrary.BlockGeometry;
import com.minecraftclone.world.World;

public class Block {

    private final boolean solid;
    private final String topTex;
    private final String bottomTex;
    private final String sideTex;
    private final BlockGeometry geometry;
    private final BlockType type;

    /**
     * Full constructor with custom geometry
     */
    public Block(boolean solid, String topTex, String sideTex, String bottomTex, BlockGeometry geometry, BlockType type) {
        this.solid = solid;
        this.topTex = topTex;
        this.sideTex = sideTex;
        this.bottomTex = bottomTex;
        this.geometry = geometry;
        this.type = type;
    }

    /**
     * Constructor for standard cube blocks with different textures
     */
    public Block(boolean solid, String topTex, String sideTex, String bottomTex) {
        this(solid, topTex, sideTex, bottomTex, MeshLibrary.CUBE, BlockType.CUBE);
    }

    /**
     * Constructor for blocks with same texture on all sides
     */
    public Block(boolean solid, String texture) {
        this(solid, texture, texture, texture, MeshLibrary.CUBE, BlockType.CUBE);
    }

    /**
     * Constructor for cube blocks with custom geometry type
     */
    public Block(boolean solid, String texture, BlockGeometry geometry, BlockType type) {
        this(solid, texture, texture, texture, geometry, type);
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isBreakable() {
        //NOTE: always returns true for now
        return true;
    }

    /**
     * returns true, overwritten by child classes for block specific rules
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public boolean canBePlacedAt(World world, int x, int y, int z) {
        return true;
    }

    public String getTopTex() {
        return topTex;
    }

    public String getBottomTex() {
        return bottomTex;
    }

    public String getSideTex() {
        return sideTex;
    }

    public BlockGeometry getGeometry() {
        return geometry;
    }

    public BlockType getType() {
        return type;
    }

    /**
     * Enum for different block types
     */
    public enum BlockType {
        CUBE,
        STAIRS,
        SLAB,
        FENCE,
        CUSTOM,
    }
}
