package com.minecraftclone.block;

import com.minecraftclone.block.Block.BlockType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

/**
 * loads all block definitions from data/blocks.yml at startup.
 * provides block instances by string ID and the full texture list for BlockAtlas.
 * replaces the hardcoded Blocks.java constants — use BlockRegistry.get("id") instead.
 */
public final class BlockRegistry {

    private static final Logger LOGGER = Logger.getLogger(BlockRegistry.class.getName());

    //IS: path to block definition file, relative to resources root
    private static final String BLOCKS_FILE = "data/blocks.yml";

    //IS: map of block id -> Block instance, insertion-ordered to match blocks.yml
    private static final Map<String, Block> REGISTRY = new LinkedHashMap<>();

    //IS: ordered set of all unique texture names used across all blocks
    //INFO: LinkedHashSet preserves insertion order, which determines atlas tile layout
    //INFO: used by BlockAtlas.build() to know which textures to stitch
    private static final Set<String> ALL_TEXTURES = new LinkedHashSet<>();

    private static boolean loaded = false;

    /**
     * parses blocks.yml and registers all blocks and their textures.
     * must be called before BlockAtlas.build() or any BlockRegistry.get() call.
     * safe to call multiple times — only runs on first call.
     */
    public static void load() {
        if (loaded) return;
        loaded = true;

        InputStream stream = BlockRegistry.class.getClassLoader().getResourceAsStream(BLOCKS_FILE);
        if (stream == null) {
            throw new RuntimeException("Block definition file not found in resources: " + BLOCKS_FILE);
        }

        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(stream);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> blockList = (List<Map<String, Object>>) root.get("blocks");

        if (blockList == null) {
            throw new RuntimeException("blocks.yml is missing a top-level 'blocks' list");
        }

        for (Map<String, Object> entry : blockList) {
            try {
                String id = required(entry, "id");
                Block block = parseBlock(id, entry);
                REGISTRY.put(id, block);
            } catch (Exception e) {
                LOGGER.warning("BlockRegistry: failed to load block entry " + entry + " — " + e.getMessage());
            }
        }

        LOGGER.info("BlockRegistry: loaded " + REGISTRY.size() + " blocks, " + ALL_TEXTURES.size() + " unique textures");
    }

    /**
     * returns a block by its registry ID as defined in blocks.yml.
     * @param id block ID string
     * @return Block instance
     * @throws IllegalArgumentException if the ID is not registered
     */
    public static Block get(String id) {
        Block block = REGISTRY.get(id);
        if (block == null) throw new IllegalArgumentException("Unknown block ID: '" + id + "'");
        return block;
    }

    /**
     * returns all unique texture names used by all registered blocks, in encounter order.
     * this order determines tile positions in the atlas — do not rely on it being stable
     * across different runs if blocks.yml is edited.
     * @return unmodifiable ordered set of texture names (without .png extension)
     */
    public static Set<String> getAllTextures() {
        return Collections.unmodifiableSet(ALL_TEXTURES);
    }

    /**
     * returns all registered blocks as an unmodifiable map.
     * @return id -> Block map
     */
    public static Map<String, Block> getAll() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    /**
     * parses one block entry from the YAML map and registers its textures.
     * @param id    the block's id field (already extracted)
     * @param entry the raw YAML map for this block
     * @return constructed Block instance
     */
    private static Block parseBlock(String id, Map<String, Object> entry) {
        //IS: block shape type — controls which MeshLibrary geometry is used
        BlockType type = BlockType.CUBE;
        if (entry.containsKey("type")) {
            String typeStr = (String) entry.get("type");
            try {
                type = BlockType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("BlockRegistry: unknown type '" + typeStr + "' for block '" + id + "', defaulting to CUBE");
            }
        }

        //IS: whether the block fills its entire 1x1x1 volume, used for face occlusion culling
        //INFO: must be false for stairs, slabs, fences, and any other partial-volume block
        boolean full = entry.containsKey("full") ? (boolean) entry.get("full") : true;

        //IS: whether the player can break this block
        boolean breakable = entry.containsKey("breakable") ? (boolean) entry.get("breakable") : true;

        //DOES: resolve per-face texture names
        //INFO: 'texture' sets the fallback for all faces
        //INFO: 'top', 'side', 'bottom' override individually
        if (!entry.containsKey("texture")) {
            throw new IllegalArgumentException("block '" + id + "' has no 'texture' field");
        }
        String fallback = (String) entry.get("texture");
        String top    = entry.containsKey("top")    ? (String) entry.get("top")    : fallback;
        String side   = entry.containsKey("side")   ? (String) entry.get("side")   : fallback;
        String bottom = entry.containsKey("bottom") ? (String) entry.get("bottom") : fallback;

        //DOES: add all textures used by this block to the global set
        //INFO: LinkedHashSet deduplicates while preserving insertion order
        ALL_TEXTURES.add(top);
        ALL_TEXTURES.add(side);
        ALL_TEXTURES.add(bottom);

        return new Block(full, top, side, bottom, type, breakable);
    }

    /**
     * extracts a required string field from a YAML entry map.
     * @throws IllegalArgumentException if the field is absent
     */
    private static String required(Map<String, Object> entry, String key) {
        Object val = entry.get(key);
        if (val == null) throw new IllegalArgumentException("missing required field '" + key + "'");
        return val.toString();
    }

    private BlockRegistry() {}
}
