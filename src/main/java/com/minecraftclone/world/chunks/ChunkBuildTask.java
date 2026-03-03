package com.minecraftclone.world.chunks;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.minecraftclone.block.Block;
import com.minecraftclone.render.ChunkMeshBuilder;
import java.util.concurrent.Callable;

public class ChunkBuildTask implements Callable<ChunkBuildResult> {

    private final ChunkPos pos;
    private final Chunk chunk;
    private final Block[][][] neighborUp;
    private final Block[][][] neighborDown;
    private final Block[][][] neighborNorth;
    private final Block[][][] neighborSouth;
    private final Block[][][] neighborEast;
    private final Block[][][] neighborWest;
    private final boolean buildCollision;
    private final boolean generateTerrain;

    public ChunkBuildTask(
        ChunkPos pos,
        Chunk chunk,
        Block[][][] neighborUp,
        Block[][][] neighborDown,
        Block[][][] neighborNorth,
        Block[][][] neighborSouth,
        Block[][][] neighborEast,
        Block[][][] neighborWest,
        boolean buildCollision,
        boolean generateTerrain
    ) {
        this.pos = pos;
        this.chunk = chunk;
        this.neighborUp = neighborUp;
        this.neighborDown = neighborDown;
        this.neighborNorth = neighborNorth;
        this.neighborSouth = neighborSouth;
        this.neighborEast = neighborEast;
        this.neighborWest = neighborWest;
        this.buildCollision = buildCollision;
        this.generateTerrain = generateTerrain;
    }

    @Override
    public ChunkBuildResult call() {
        //DOES: stage 1 - generate terrain data into chunk block array
        if (generateTerrain) {
            TerrainGenerator.generateChunk(chunk);
        }

        //DOES: snapshot the chunk's own blocks array to avoid data races with the main thread
        //INFO: neighbor arrays are already snapshotted by ChunkManager before task submission
        Block[][][] src = chunk.getBlocks();
        Block[][][] blocks = new Block[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                System.arraycopy(src[x][y], 0, blocks[x][y], 0, Chunk.SIZE);
            }
        }

        //DOES: stage 2 - build single merged mesh using greedy meshing and atlas UVs
        Mesh mesh = ChunkMeshBuilder.build(
            blocks,
            neighborUp,
            neighborDown,
            neighborNorth,
            neighborSouth,
            neighborEast,
            neighborWest
        );

        //DOES: stage 3 - build collision shape from mesh geometry (pure math, no physics space touch)
        CollisionShape shape = null;
        if (buildCollision && mesh.getVertexCount() > 0) {
            Node tempNode = new Node();
            tempNode.attachChild(new Geometry("col", mesh));
            shape = CollisionShapeFactory.createMeshShape(tempNode);
        }

        return new ChunkBuildResult(pos, chunk, mesh, shape);
    }
}
