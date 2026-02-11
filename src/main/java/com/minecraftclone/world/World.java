package com.minecraftclone.world;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.minecraftclone.block.Block;
import com.minecraftclone.player.PlayerCharacter;
import com.minecraftclone.player.input.ActionInput;
import com.minecraftclone.player.input.AnalogInput;
import com.minecraftclone.world.chunks.Chunk;
import com.minecraftclone.world.chunks.ChunkManager;
import com.minecraftclone.world.chunks.ChunkPos;
import java.util.HashMap;
import java.util.Map;

public class World {

    private static final int RENDER_DISTANCE = 50;
    private final SimpleApplication app;
    private final PlayerCharacter playerCharacter;
    private final BulletAppState bulletAppState;
    private final ChunkManager chunkManager;

    private final Map<String, Chunk> chunks = new HashMap<>();

    public World(SimpleApplication app, ActionInput actionInput, AnalogInput analogInput, BulletAppState bulletAppState) {
        this.app = app;
        this.bulletAppState = bulletAppState;

        //DOES: create & attatch player
        playerCharacter = new PlayerCharacter(bulletAppState, actionInput, app);
        app.getRootNode().attachChild(playerCharacter.getNode());

        chunkManager = new ChunkManager(app, this, RENDER_DISTANCE); // render distance
    }

    public Block getBlock(int worldX, int worldY, int worldZ) {
        Chunk chunk = getChunk(worldX, worldY, worldZ);
        if (chunk == null) return null;

        //DOES: calculate chunk block is in and request it
        return chunk.getBlock(Math.floorMod(worldX, Chunk.SIZE), Math.floorMod(worldY, Chunk.SIZE), Math.floorMod(worldZ, Chunk.SIZE));
    }

    public boolean isBlockLoaded(int wx, int wy, int wz) {
        //DOES: return bool if chunk can be gotten
        return getChunk(wx, wy, wz) != null;
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

        //TODO: figure out what ts is
        Chunk chunk = chunks.computeIfAbsent(key(chunkX, chunkY, chunkZ), k -> {
            Chunk c = new Chunk(chunkX, chunkY, chunkZ, app.getAssetManager());
            app.getRootNode().attachChild(c.getNode());
            return c;
        });

        chunk.setBlock(localX, localY, localZ, block);
        chunk.rebuild(bulletAppState.getPhysicsSpace());

        rebuildNeighborsIfNeeded(chunkX, chunkY, chunkZ, localX, localY, localZ);
    }

    /**Returns chunk at world coordinates
     * @param worldX
     * @param worldY
     * @param worldZ
     * @return
     */
    private Chunk getChunk(int worldX, int worldY, int worldZ) {
        int chunkX = Math.floorDiv(worldX, Chunk.SIZE);
        int chunkY = Math.floorDiv(worldY, Chunk.SIZE);
        int chunkZ = Math.floorDiv(worldZ, Chunk.SIZE);
        return chunks.get(key(chunkX, chunkY, chunkZ));
    }

    private void rebuildNeighborsIfNeeded(int cx, int cy, int cz, int lx, int ly, int lz) {
        if (lx == 0) rebuild(cx - 1, cy, cz);
        if (lx == Chunk.SIZE - 1) rebuild(cx + 1, cy, cz);
        if (lz == 0) rebuild(cx, cy, cz - 1);
        if (lz == Chunk.SIZE - 1) rebuild(cx, cy, cz + 1);
        if (ly == 0) rebuild(cx, cy - 1, cz);
        if (ly == Chunk.SIZE - 1) rebuild(cx, cy + 1, cz);
    }

    private void rebuild(int cx, int cy, int cz) {
        Chunk c = chunks.get(key(cx, cy, cz));
        if (c != null) {
            c.rebuild(bulletAppState.getPhysicsSpace());
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
    }

    public boolean hasChunk(ChunkPos pos) {
        return chunks.containsKey(key(pos.x, pos.y, pos.z));
    }

    public void addChunk(Chunk chunk) {
        chunks.put(key(chunk.getChunkX(), chunk.getChunkY(), chunk.getChunkZ()), chunk);
    }
}
