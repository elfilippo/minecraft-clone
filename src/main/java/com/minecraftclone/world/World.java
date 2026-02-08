package com.minecraftclone.world;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.minecraftclone.block.Block;
import com.minecraftclone.player.PlayerCharacter;
import com.minecraftclone.player.input.ActionInput;
import com.minecraftclone.player.input.AnalogInput;
import com.minecraftclone.player.input.KeyMapping;
import com.minecraftclone.world.chunks.Chunk;
import com.minecraftclone.world.chunks.ChunkManager;
import com.minecraftclone.world.chunks.ChunkPos;
import java.util.HashMap;
import java.util.Map;

public class World {

    private final SimpleApplication app;
    private final PlayerCharacter playerCharacter;
    private final BulletAppState bulletAppState;
    private final ChunkManager chunkManager;

    private final Map<String, Chunk> chunks = new HashMap<>();

    // =========================
    // CONSTRUCTOR
    // =========================

    public World(SimpleApplication app, ActionInput actionInput, AnalogInput analogInput) {
        this.app = app;
        bulletAppState = app.getStateManager().getState(BulletAppState.class);

        new KeyMapping(app.getInputManager(), actionInput, analogInput);
        playerCharacter = new PlayerCharacter(bulletAppState, actionInput, app.getCamera());
        app.getRootNode().attachChild(playerCharacter.getNode());
        chunkManager = new ChunkManager(app, this, 20); // render distance
    }

    // =========================
    // BLOCK ACCESS
    // =========================

    public Block getBlock(int wx, int wy, int wz) {
        Chunk c = getChunkAtWorld(wx, wy, wz);
        if (c == null) return null;

        return c.getBlock(Math.floorMod(wx, Chunk.SIZE), Math.floorMod(wy, Chunk.SIZE), Math.floorMod(wz, Chunk.SIZE));
    }

    public boolean isBlockLoaded(int wx, int wy, int wz) {
        return getChunkAtWorld(wx, wy, wz) != null;
    }

    public void setBlock(int wx, int wy, int wz, Block block) {
        int cx = Math.floorDiv(wx, Chunk.SIZE);
        int cy = Math.floorDiv(wy, Chunk.SIZE);
        int cz = Math.floorDiv(wz, Chunk.SIZE);

        int lx = Math.floorMod(wx, Chunk.SIZE);
        int ly = Math.floorMod(wy, Chunk.SIZE);
        int lz = Math.floorMod(wz, Chunk.SIZE);

        Chunk chunk = chunks.computeIfAbsent(key(cx, cy, cz), k -> {
            Chunk c = new Chunk(cx, cy, cz, app.getAssetManager());
            app.getRootNode().attachChild(c.getNode());
            return c;
        });

        chunk.setBlock(lx, ly, lz, block);
        chunk.rebuild(bulletAppState.getPhysicsSpace());

        rebuildNeighborsIfNeeded(cx, cy, cz, lx, ly, lz);
    }

    // =========================
    // HELPERS
    // =========================

    private Chunk getChunkAtWorld(int wx, int wy, int wz) {
        int cx = Math.floorDiv(wx, Chunk.SIZE);
        int cy = Math.floorDiv(wy, Chunk.SIZE);
        int cz = Math.floorDiv(wz, Chunk.SIZE);
        return chunks.get(key(cx, cy, cz));
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
