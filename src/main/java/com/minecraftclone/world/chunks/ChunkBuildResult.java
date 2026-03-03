package com.minecraftclone.world.chunks;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.scene.Mesh;
import java.util.Map;

public class ChunkBuildResult {

    public final ChunkPos pos;
    public final Chunk chunk;
    public final Map<String, Mesh> meshes;
    public final CollisionShape collisionShape;

    public ChunkBuildResult(ChunkPos pos, Chunk chunk, Map<String, Mesh> meshes, CollisionShape collisionShape) {
        this.pos = pos;
        this.chunk = chunk;
        this.meshes = meshes;
        this.collisionShape = collisionShape;
    }
}
