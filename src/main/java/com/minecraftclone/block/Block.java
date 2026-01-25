package com.minecraftclone.block;

public class Block {

    private final int textureIndex;
    private final boolean solid;

    public Block(int textureIndex, boolean solid) {
        this.textureIndex = textureIndex;
        this.solid = solid;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public boolean isSolid() {
        return solid;
    }
}
