package com.minecraftclone.world;

import com.minecraftclone.block.Block;
import com.minecraftclone.block.Blocks;

public final class TerrainGenerator {

    // tweak these freely
    private static final int BASE_HEIGHT = 8;
    private static final float HEIGHT_SCALE = 0.15f;
    private static final int MAX_HEIGHT = Chunk.SIZE - 1;

    private TerrainGenerator() {}

    public static void generateChunk(Chunk chunk) {
        Block[][][] blocks = chunk.getBlocks();

        int worldX = chunk.getChunkX() * Chunk.SIZE;
        int worldZ = chunk.getChunkZ() * Chunk.SIZE;

        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int z = 0; z < Chunk.SIZE; z++) {
                int height = getHeight(worldX + x, worldZ + z);
                height = Math.min(height, MAX_HEIGHT);

                for (int y = 0; y <= height; y++) {
                    if (y == height) {
                        blocks[x][y][z] = Blocks.GRASS_BLOCK;
                    } else if (y >= height - 3) {
                        blocks[x][y][z] = Blocks.DIRT;
                    } else {
                        blocks[x][y][z] = Blocks.STONE;
                    }
                }
            }
        }
    }

    // simple & fast height function
    private static int getHeight(int x, int z) {
        double h = Math.sin(x * HEIGHT_SCALE) * 2 + Math.cos(z * HEIGHT_SCALE) * 2;

        return BASE_HEIGHT + (int) h;
    }
}
