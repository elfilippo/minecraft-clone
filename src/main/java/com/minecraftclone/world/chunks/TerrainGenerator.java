package com.minecraftclone.world.chunks;

import com.minecraftclone.block.Block;
import com.minecraftclone.block.BlockRegistry;

public final class TerrainGenerator {

    //INFO: terrain generator is temporary, will be completely replaced later

    //IS: base terrain height in blocks
    private static final int BASE_HEIGHT = 8;

    //IS: controls how spread out the hills are
    private static final float HEIGHT_SCALE = 0.15f;

    //IS: maximum height terrain can reach within a chunk
    private static final int MAX_HEIGHT = Chunk.SIZE - 1;

    //IS: cached block references from registry (avoids repeated map lookups per block)
    private static final Block GRASS = BlockRegistry.get("grass_block");
    private static final Block DIRT  = BlockRegistry.get("dirt");
    private static final Block STONE = BlockRegistry.get("stone");

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
                        blocks[x][y][z] = GRASS;
                    } else if (y >= height - 3) {
                        blocks[x][y][z] = DIRT;
                    } else {
                        blocks[x][y][z] = STONE;
                    }
                }
            }
        }
    }

    //IS: simple deterministic height function based on sine/cosine waves
    private static int getHeight(int x, int z) {
        double h = Math.sin(x * HEIGHT_SCALE) * 2 + Math.cos(z * HEIGHT_SCALE) * 2;
        return BASE_HEIGHT + (int) h;
    }
}
