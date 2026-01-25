package com.minecraftclone.world;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;
import com.minecraftclone.block.Block;
import java.util.HashMap;
import java.util.Map;

public class World {

    private final Node rootNode;
    private final PhysicsSpace physicsSpace;

    private final Map<String, Chunk> chunks = new HashMap<>();

    public World(Node rootNode, PhysicsSpace physicsSpace) {
        this.rootNode = rootNode;
        this.physicsSpace = physicsSpace;
    }

    public void placeBlock(int wx, int wy, int wz, Block block) {
        int cx = Math.floorDiv(wx, Chunk.SIZE);
        int cy = Math.floorDiv(wy, Chunk.SIZE);
        int cz = Math.floorDiv(wz, Chunk.SIZE);

        int lx = Math.floorMod(wx, Chunk.SIZE);
        int ly = Math.floorMod(wy, Chunk.SIZE);
        int lz = Math.floorMod(wz, Chunk.SIZE);

        String key = cx + "," + cy + "," + cz;

        Chunk chunk = chunks.get(key);
        if (chunk == null) {
            chunk = new Chunk(cx, cy, cz);
            chunks.put(key, chunk);
            rootNode.attachChild(chunk.getNode());
        }

        chunk.setBlock(lx, ly, lz, block);
        chunk.rebuild(physicsSpace);
    }
}
