package com.minecraftclone.world.chunks;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.scene.Mesh;
import java.util.Map;

public record ChunkBuildResult(ChunkPos pos, Chunk chunk, Map<String, Mesh> meshes, CollisionShape collisionShape) {}
