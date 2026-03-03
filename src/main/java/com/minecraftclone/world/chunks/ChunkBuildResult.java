package com.minecraftclone.world.chunks;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.scene.Mesh;

public class ChunkBuildResult {

    public final ChunkPos pos;
    public final Chunk chunk;

    //IS: single merged mesh for the whole chunk (one draw call)
    public final Mesh mesh;

    public final CollisionShape collisionShape;

    public ChunkBuildResult(ChunkPos pos, Chunk chunk, Mesh mesh, CollisionShape collisionShape) {
        this.pos = pos;
        this.chunk = chunk;
        this.mesh = mesh;
        this.collisionShape = collisionShape;
    }
}
