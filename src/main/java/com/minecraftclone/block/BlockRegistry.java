package com.minecraftclone.block;

import java.util.HashMap;
import java.util.Map;

public final class BlockRegistry {

    private static final Map<String, Block> BLOCKS = new HashMap<>();

    public static Block register(String id, String texture) {
        Block block = new Block(id, texture);
        BLOCKS.put(id, block);
        return block;
    }

    public static Block get(String id) {
        return BLOCKS.get(id);
    }
}
