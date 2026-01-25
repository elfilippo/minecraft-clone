package com.minecraftclone.world;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.minecraftclone.block.Block;

public class Chunk {

    public static final int SIZE = 32;

    private final int chunkX, chunkY, chunkZ;

    private final Block[][][] blocks = new Block[SIZE][SIZE][SIZE];
    private final Node chunkNode = new Node("Chunk");

    private Geometry geometry;
    private RigidBodyControl collisionBody;
    private boolean dirty = true;

    public Chunk(int chunkX, int chunkY, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;

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

        if (collisionBody != null) {
            physicsSpace.remove(collisionBody);
            geometry.removeControl(collisionBody);
        }

        Mesh mesh = ChunkMeshBuilder.build(blocks);

        if (geometry == null) {
            geometry = new Geometry("ChunkMesh", mesh);
            geometry.setMaterial(ChunkMaterials.DEFAULT);
            chunkNode.attachChild(geometry);
        } else {
            geometry.setMesh(mesh);
        }

        CollisionShape shape = CollisionShapeFactory.createMeshShape(geometry);

        collisionBody = new RigidBodyControl(shape, 0f);
        geometry.addControl(collisionBody);
        physicsSpace.add(collisionBody);

        dirty = false;
    }
}
