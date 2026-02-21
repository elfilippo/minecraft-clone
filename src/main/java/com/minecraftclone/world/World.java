package com.minecraftclone.world;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.minecraftclone.Main;
import com.minecraftclone.block.Block;
import com.minecraftclone.player.PlayerCharacter;
import com.minecraftclone.player.input.ActionInput;
import com.minecraftclone.player.input.AnalogInput;
import com.minecraftclone.render.RenderEngine;
import com.minecraftclone.world.chunks.Chunk;
import com.minecraftclone.world.chunks.ChunkManager;
import com.minecraftclone.world.chunks.ChunkPos;
import java.util.HashMap;
import java.util.Map;

public class World {

    private static final int RENDER_DISTANCE = 10;
    private final SimpleApplication app;
    private final PlayerCharacter playerCharacter;
    private final BulletAppState bulletAppState;
    private final ChunkManager chunkManager;
    private final RenderEngine render;

    private final Map<String, Chunk> chunks = new HashMap<>();

    public World(Main app, ActionInput actionInput, AnalogInput analogInput, BulletAppState bulletAppState) {
        this.app = app;
        this.bulletAppState = bulletAppState;

        //DOES: create & attatch player
        playerCharacter = new PlayerCharacter(bulletAppState, actionInput, app);
        render = new RenderEngine(app, playerCharacter);
        app.getRootNode().attachChild(playerCharacter.getNode());

        chunkManager = new ChunkManager(app, this, RENDER_DISTANCE);
    }

    public Block getBlock(int worldX, int worldY, int worldZ) {
        Chunk chunk = getChunk(worldX, worldY, worldZ);
        if (chunk == null) return null;

        //DOES: calculate chunk block is in and request it
        return chunk.getBlock(
            Math.floorMod(worldX, Chunk.SIZE),
            Math.floorMod(worldY, Chunk.SIZE),
            Math.floorMod(worldZ, Chunk.SIZE)
        );
    }

    public boolean isBlockLoaded(int worldX, int worldY, int worldZ) {
        //DOES: return bool if chunk can be gotten
        return getChunk(worldX, worldY, worldZ) != null;
    }

    /**Sets block at world coordinates
     * @param worldX
     * @param worldY
     * @param worldZ
     * @param block
     */
    public void setBlock(int worldX, int worldY, int worldZ, Block block) {
        //DOES: get chunk coords
        int chunkX = Math.floorDiv(worldX, Chunk.SIZE);
        int chunkY = Math.floorDiv(worldY, Chunk.SIZE);
        int chunkZ = Math.floorDiv(worldZ, Chunk.SIZE);

        //DOES: get coords within the chunks
        int localX = Math.floorMod(worldX, Chunk.SIZE);
        int localY = Math.floorMod(worldY, Chunk.SIZE);
        int localZ = Math.floorMod(worldZ, Chunk.SIZE);

        //DOES: get chunk from map, create & attatch to root node if doesn't exist
        Chunk chunk = chunks.computeIfAbsent(key(chunkX, chunkY, chunkZ), k -> {
            Chunk c = new Chunk(chunkX, chunkY, chunkZ, app.getAssetManager());
            app.getRootNode().attachChild(c.getNode());
            return c;
        });

        chunk.setBlock(localX, localY, localZ, block);
        chunk.rebuild(bulletAppState.getPhysicsSpace());

        rebuildNeighborsIfNeeded(chunkX, chunkY, chunkZ, localX, localY, localZ);
    }

    /**returns chunk at world coordinates
     * @param worldX
     * @param worldY
     * @param worldZ
     * @return Chunk, null if doesn't exist
     */
    private Chunk getChunk(int worldX, int worldY, int worldZ) {
        int chunkX = Math.floorDiv(worldX, Chunk.SIZE);
        int chunkY = Math.floorDiv(worldY, Chunk.SIZE);
        int chunkZ = Math.floorDiv(worldZ, Chunk.SIZE);
        return chunks.get(key(chunkX, chunkY, chunkZ));
    }

    /**
     * checks if position is bordering another chunk and rebuilds
     * @param chunkX
     * @param chunkY
     * @param chunkZ
     * @param localX
     * @param localY
     * @param localZ
     */
    private void rebuildNeighborsIfNeeded(int chunkX, int chunkY, int chunkZ, int localX, int localY, int localZ) {
        if (localX == 0) rebuild(chunkX - 1, chunkY, chunkZ);
        if (localX == Chunk.SIZE - 1) rebuild(chunkX + 1, chunkY, chunkZ);
        if (localZ == 0) rebuild(chunkX, chunkY, chunkZ - 1);
        if (localZ == Chunk.SIZE - 1) rebuild(chunkX, chunkY, chunkZ + 1);
        if (localY == 0) rebuild(chunkX, chunkY - 1, chunkZ);
        if (localY == Chunk.SIZE - 1) rebuild(chunkX, chunkY + 1, chunkZ);
    }

    /**
     * rebuilds chunk if exists
     * @param chunkX
     * @param chunkY
     * @param chunkZ
     */
    private void rebuild(int chunkX, int chunkY, int chunkZ) {
        Chunk chunk = chunks.get(key(chunkX, chunkY, chunkZ));
        if (chunk != null) {
            chunk.rebuild(bulletAppState.getPhysicsSpace());
        }
    }

    static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public void update() {
        chunkManager.update(playerCharacter.getPlayerControl().getPhysicsLocation());
        render.guiUpdate();
    }

    /**
     * checks if chunk is in hashmap (exists)
     * @param pos
     * @return
     */
    public boolean hasChunk(ChunkPos pos) {
        return chunks.containsKey(key(pos.x, pos.y, pos.z));
    }

    /**
     * adds chunk to hashmap
     * @param chunk
     */
    public void addChunk(Chunk chunk) {
        chunks.put(key(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ()), chunk);
    }
}
