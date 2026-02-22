package com.minecraftclone.world.chunks;

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

    //IS: chunk size
    public static final int SIZE = 32;

    private final int chunkX, chunkY, chunkZ;

    //IS: block object stored in triple array of ints (local pos)
    private final Block[][][] blocks = new Block[SIZE][SIZE][SIZE];

    private final Node chunkNode = new Node("Chunk");
    private final Node collisionNode = new Node("Collision");
    private final AssetManager assetManager;

    private final Map<String, Geometry> geometries = new HashMap<>();
    private RigidBodyControl collisionBody;
    private final PhysicsSpace physicsSpace;

    private boolean dirty = true;

    public Chunk(int chunkX, int chunkY, int chunkZ, AssetManager assetManager, PhysicsSpace physicsSpace) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;

        //DOES: set location of the chunk node
        chunkNode.setLocalTranslation(chunkX * SIZE, chunkY * SIZE, chunkZ * SIZE);
    }

    public Node getNode() {
        return chunkNode;
    }

    /**
     * saves block into blocks array & dirties chunk to reload mesh
     * @param x
     * @param y
     * @param z
     * @param block
     */
    public void setBlock(int x, int y, int z, Block block) {
        blocks[x][y][z] = block;
        dirty = true;
    }

    /**
     * returns the block object at local pos
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Block getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    /**
     * rebuilds the chunk mesh if not dirty
     * <p>
     */
    public void rebuild() {
        if (!dirty) return;

        Map<String, Mesh> meshes = ChunkMeshBuilder.build(blocks);
        //DOES: remove old geometries
        for (Geometry geometry : geometries.values()) {
            geometry.removeFromParent();
        }
        geometries.clear();

        //DOES: if exists, remove collision body
        removeCollision();
        collisionNode.detachAllChildren();

        //DOES: iterate over meshes hashmap & create and add geometries
        for (Map.Entry<String, Mesh> meshEntry : meshes.entrySet()) {
            //DOES: create geometry with chunk_<coords> as name and mesh hashmap value as mesh
            var geometry = new Geometry("chunk_" + meshEntry.getKey(), meshEntry.getValue());

            //DOES: set material of geometry by taking it out of BlockMaterialCashe
            geometry.setMaterial(BlockMaterialCache.get(meshEntry.getKey(), assetManager));

            //DOES: add geometry to geometries hashmap
            geometries.put(meshEntry.getKey(), geometry);

            //DOES: attatch geometry to chunkNode and collisionNode
            chunkNode.attachChild(geometry);
            collisionNode.attachChild(geometry.clone());
        }

        //DOES: set chunk to clean to indicate completion of rebuild
        dirty = false;
    }

    public void addCollision() {
        //CASE: when chunk has geometry at collision node
        if (collisionNode.getQuantity() > 0) {
            //DOES: create collision body
            CollisionShape shape = CollisionShapeFactory.createMeshShape(collisionNode);
            collisionBody = new RigidBodyControl(shape, 0f);
            chunkNode.addControl(collisionBody);
            physicsSpace.add(collisionBody);
        }
    }

    /**
     * removes collision from the chunk if it has any
     */
    public void removeCollision() {
        if (collisionBody != null) {
            physicsSpace.remove(collisionBody);
        }
    }

    /**
     * unloads the chunk and removes collision
     */
    public void unload() {
        for (Geometry geometry : geometries.values()) {
            geometry.removeFromParent();
        }
        geometries.clear();

        removeCollision();
    }

    /**
     * returns the array of block objects
     * @return
     */
    public Block[][][] getBlocks() {
        return blocks;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public int getChunkY() {
        return chunkY;
    }

    /**
     * sets if the chunk is dirty (won't rebuild otherwise)
     * @param dirty
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
