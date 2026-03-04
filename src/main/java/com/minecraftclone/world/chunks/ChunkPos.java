package com.minecraftclone.world.chunks;

public final class ChunkPos {

    public final int x, y, z;

    public ChunkPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkPos)) return false;
        ChunkPos p = (ChunkPos) o;
        return x == p.x && y == p.y && z == p.z;
    }

    @Override
    public int hashCode() {
        int h = x;
        h = 31 * h + y;
        h = 31 * h + z;
        return h;
    }
}
