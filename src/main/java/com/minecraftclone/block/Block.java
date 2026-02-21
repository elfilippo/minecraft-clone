package com.minecraftclone.block;

import com.minecraftclone.block.MeshLibrary.BlockGeometry;
import com.minecraftclone.world.World;

public class Block {

    private final boolean full;
    private final String topTex;
    private final String bottomTex;
    private final String sideTex;
    private final BlockGeometry geometry;
    private final BlockType type;

    /**
     * full constructor with sided textures and custom block type
     */
    public Block(boolean full, String topTex, String sideTex, String bottomTex, BlockType type) {
        this.full = full;
        this.topTex = topTex;
        this.sideTex = sideTex;
        this.bottomTex = bottomTex;
        this.type = type;
        this.geometry = getBlockGeometry(type);
    }

    /**
     * constructor for cube blocks with sided textures
     */
    public Block(boolean full, String topTex, String sideTex, String bottomTex) {
        this(full, topTex, sideTex, bottomTex, BlockType.CUBE);
    }

    /**
     * constructor for cube blocks with same texture on all sides
     */
    public Block(boolean full, String texture) {
        this(full, texture, texture, texture, BlockType.CUBE);
    }

    /**
     * constructor for blocks with custom block type
     */
    public Block(boolean full, String texture, BlockType type) {
        this(full, texture, texture, texture, type);
    }

    public boolean isFull() {
        return full;
    }

    public boolean isBreakable() {
        //NOTE: always returns true for now, hardness to be added later
        return true;
    }

    /**
     * default true, overridden by child classes for block-specific rules
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
     * gets block geometry from mesh library
     * @param type
     * @return
     */
    private BlockGeometry getBlockGeometry(BlockType type) {
        //NOTE: needs to have extra logic for rotateable blocks
        switch (type) {
            case CUBE:
                return MeshLibrary.CUBE;
            case STAIRS:
                return MeshLibrary.STAIRS_NORTH;
            case SLAB:
                return MeshLibrary.SLAB;
            case FENCE:
                return MeshLibrary.FENCE_POST;
            case CUSTOM:
                return MeshLibrary.CUBE;
            default:
                return MeshLibrary.CUBE;
        }
    }

    /**
     * enum for different block types
     */
    public enum BlockType {
        CUBE,
        STAIRS,
        SLAB,
        FENCE,
        CUSTOM,
    }
}
