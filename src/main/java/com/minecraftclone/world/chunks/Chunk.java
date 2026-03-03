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
import com.minecraftclone.render.BlockAtlas;
import com.minecraftclone.render.ChunkMeshBuilder;
import com.minecraftclone.world.World;

public class Chunk {

    //IS: chunk size
    public static final int SIZE = 32;

    private final int chunkX, chunkY, chunkZ;

    //IS: block objects stored in triple array indexed by local position
    private final Block[][][] blocks = new Block[SIZE][SIZE][SIZE];

    private final Node chunkNode = new Node("Chunk");

    //IS: collision node holds cloned geometry for physics shape building
    private final Node collisionNode = new Node("Collision");

    private final AssetManager assetManager;

    //IS: single geometry for the whole chunk — one draw call per chunk
    private Geometry chunkGeometry;

    private RigidBodyControl collisionBody;
    private final PhysicsSpace physicsSpace;
    private final World world;

    private boolean dirty = true;

    public Chunk(
        World world,
        int chunkX,
        int chunkY,
        int chunkZ,
        AssetManager assetManager,
        PhysicsSpace physicsSpace
    ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;

        //DOES: set location of the chunk node to its world position
        chunkNode.setLocalTranslation(chunkX * SIZE, chunkY * SIZE, chunkZ * SIZE);
    }

    public Node getNode() {
        return chunkNode;
    }

    /**
     * saves block into blocks array and marks chunk as dirty so mesh rebuilds
     * @param x local x
     * @param y local y
     * @param z local z
     * @param block block to set, null to clear
     */
    public void setBlock(int x, int y, int z, Block block) {
        blocks[x][y][z] = block;
        dirty = true;
    }

    /**
     * applies a pre-built mesh from a background ChunkBuildTask to the chunk node.
     * replaces the existing geometry with a single new geometry using the atlas material.
     * @param mesh the pre-built mesh
     */
    public void applyMesh(Mesh mesh) {
        //DOES: remove old geometry from scene graph if present
        if (chunkGeometry != null) {
            chunkGeometry.removeFromParent();
            chunkGeometry = null;
        }

        //CASE: empty chunk has no geometry to display
        if (mesh == null || mesh.getVertexCount() == 0) {
            dirty = false;
            return;
        }

        //DOES: create single geometry using the shared atlas material
        chunkGeometry = new Geometry("chunk", mesh);
        chunkGeometry.setMaterial(BlockAtlas.getMaterial());
        chunkNode.attachChild(chunkGeometry);

        dirty = false;
    }

    /**
     * returns the block at the given local position
     * @param x local x
     * @param y local y
     * @param z local z
     * @return block, or null if air
     */
    public Block getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    /**
     * synchronously rebuilds the chunk mesh on the calling thread if dirty.
     * used for block placement / breaking on the main thread.
     */
    public void rebuild() {
        if (!dirty) return;

        Mesh mesh = ChunkMeshBuilder.build(blocks, world, chunkX, chunkY, chunkZ);

        //DOES: remove old geometry
        if (chunkGeometry != null) {
            chunkGeometry.removeFromParent();
            chunkGeometry = null;
        }

        //DOES: remove old collision
        removeCollision();
        collisionNode.detachAllChildren();

        //CASE: empty chunk — no geometry or collision needed
        if (mesh.getVertexCount() == 0) {
            dirty = false;
            return;
        }

        //DOES: create single geometry with atlas material
        chunkGeometry = new Geometry("chunk", mesh);
        chunkGeometry.setMaterial(BlockAtlas.getMaterial());
        chunkNode.attachChild(chunkGeometry);

        //DOES: clone geometry into collision node for physics shape building
        collisionNode.attachChild(chunkGeometry.clone());

        dirty = false;
    }

    /**
     * builds and applies collision from the collision node geometry.
     * used after synchronous rebuild() on the main thread.
     */
    public void addCollision() {
        if (collisionNode.getQuantity() > 0) {
            CollisionShape shape = CollisionShapeFactory.createMeshShape(collisionNode);
            addCollision(shape);
        }
    }

    /**
     * applies a pre-built collision shape to the chunk.
     * used by ChunkManager after background build completes.
     * @param shape pre-built collision shape
     */
    public void addCollision(CollisionShape shape) {
        removeCollision();
        collisionBody = new RigidBodyControl(shape, 0f);
        chunkNode.addControl(collisionBody);
        physicsSpace.add(collisionBody);
    }

    /**
     * removes the collision body from the physics space if present
     */
    public void removeCollision() {
        if (collisionBody != null) {
            physicsSpace.remove(collisionBody);
            collisionBody = null;
        }
    }

    /**
     * unloads the chunk by removing its geometry and collision from the scene
     */
    public void unload() {
        if (chunkGeometry != null) {
            chunkGeometry.removeFromParent();
            chunkGeometry = null;
        }
        removeCollision();
    }

    /**
     * returns the raw block array (used for mesh building and neighbor snapshots)
     */
    public Block[][][] getBlocks() {
        return blocks;
    }

    public int getChunkX() { return chunkX; }
    public int getChunkY() { return chunkY; }
    public int getChunkZ() { return chunkZ; }

    /**
     * marks chunk as dirty so it will rebuild on next rebuild() call
     * @param dirty
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
