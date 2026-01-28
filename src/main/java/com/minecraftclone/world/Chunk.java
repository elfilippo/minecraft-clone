package com.minecraftclone.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.minecraftclone.block.Block;
import com.minecraftclone.render.BlockMaterialCache;
import com.minecraftclone.render.ChunkMeshBuilder;
import java.util.HashMap;
import java.util.Map;

public class Chunk {

    public static final int SIZE = 32;

    private final int chunkX, chunkY, chunkZ;

    private final Block[][][] blocks = new Block[SIZE][SIZE][SIZE];
    private final Node chunkNode = new Node("Chunk");
    private final AssetManager assetManager;

    private final Map<String, Geometry> geometries = new HashMap<>();
    private RigidBodyControl collisionBody;
    private boolean dirty = true;

    public Chunk(int chunkX, int chunkY, int chunkZ, AssetManager assetManager) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.assetManager = assetManager;

        chunkNode.setLocalTranslation(chunkX * SIZE, chunkY * SIZE, chunkZ * SIZE);
    }

    public Node getNode() {
        return chunkNode;
    }

    public void setBlock(int x, int y, int z, Block block) {
        blocks[x][y][z] = block;
        dirty = true;
    }

    public Block getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public void rebuild(PhysicsSpace physicsSpace) {
        if (!dirty) return;

        Map<String, Mesh> meshes = ChunkMeshBuilder.build(blocks);

        // remove old geometries
        for (Geometry g : geometries.values()) {
            g.removeFromParent();
        }
        geometries.clear();

        if (collisionBody != null) {
            physicsSpace.remove(collisionBody);
        }

        Node collisionNode = new Node("CollisionNode");

        for (Map.Entry<String, Mesh> e : meshes.entrySet()) {
            Geometry g = new Geometry("chunk_" + e.getKey(), e.getValue());
            g.setMaterial(BlockMaterialCache.get(e.getKey(), assetManager));
            geometries.put(e.getKey(), g);
            chunkNode.attachChild(g);
            collisionNode.attachChild(g.clone());
        }

        if (collisionNode.getQuantity() > 0) {
            CollisionShape shape = CollisionShapeFactory.createMeshShape(collisionNode);
            collisionBody = new RigidBodyControl(shape, 0f);
            chunkNode.addControl(collisionBody);
            physicsSpace.add(collisionBody);
        }

        dirty = false;
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
