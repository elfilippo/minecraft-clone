package com.minecraftclone.block;

public class Block {

    private final boolean solid;
    private final String top;
    private final String bottom;
    private final String side;

    public Block(boolean solid, String top, String side, String bottom) {
        this.solid = solid;
        this.top = top;
        this.side = side;
        this.bottom = bottom;
    }

    public Block(boolean solid, String texture) {
        this.solid = solid;
        this.top = texture;
        this.side = texture;
        this.bottom = texture;
    }

    public boolean isSolid() {
        return solid;
    }

    public String top() {
        return top;
    }

    public String bottom() {
        return bottom;
    }

    public String side() {
        return side;
    }
}
