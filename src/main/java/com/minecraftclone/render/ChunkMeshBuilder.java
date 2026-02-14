package com.minecraftclone.render;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.minecraftclone.block.Block;
import com.minecraftclone.block.MeshLibrary.BlockGeometry;
import com.minecraftclone.block.MeshLibrary.Face;
import com.minecraftclone.block.MeshLibrary.FaceDirection;
import com.minecraftclone.world.chunks.Chunk;
import java.util.*;

public final class ChunkMeshBuilder {

    public static Map<String, Mesh> build(Block[][][] blocks) {
        Map<String, List<Vector3f>> pos = new HashMap<>();
        Map<String, List<Vector3f>> norm = new HashMap<>();
        Map<String, List<Vector2f>> uv = new HashMap<>();
        Map<String, List<Integer>> idx = new HashMap<>();
        Map<String, Integer> offset = new HashMap<>();

        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                for (int z = 0; z < Chunk.SIZE; z++) {
                    Block b = blocks[x][y][z];
                    if (b == null) continue;

                    // Get the geometry definition for this block
                    BlockGeometry geometry = b.getGeometry();

                    // Add each face from the geometry
                    for (Face face : geometry.getFaces()) {
                        // Check if this face should be rendered (occlusion culling)
                        if (shouldRenderFace(face, blocks, x, y, z)) {
                            // Get the texture for this face based on its texture key
                            String texture = getTextureForFace(b, face.textureKey);

                            // Offset the face vertices to the block's position
                            Vector3f[] worldVertices = new Vector3f[4];
                            for (int i = 0; i < 4; i++) {
                                worldVertices[i] = face.vertices[i].add(x, y, z);
                            }

                            addFace(texture, pos, norm, uv, idx, offset, worldVertices, face.normal, face.uvs);
                        }
                    }
                }
            }
        }

        Map<String, Mesh> meshes = new HashMap<>();

        for (String tex : pos.keySet()) {
            var mesh = new Mesh();
            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(pos.get(tex).toArray(new Vector3f[0])));
            mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(norm.get(tex).toArray(new Vector3f[0])));
            mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(uv.get(tex).toArray(new Vector2f[0])));
            mesh.setBuffer(
                VertexBuffer.Type.Index,
                3,
                BufferUtils.createIntBuffer(
                    idx
                        .get(tex)
                        .stream()
                        .mapToInt(i -> i)
                        .toArray()
                )
            );
            mesh.updateBound();
            meshes.put(tex, mesh);
        }

        return meshes;
    }

    /**
     * Determines if a face should be rendered based on occlusion
     */
    private static boolean shouldRenderFace(Face face, Block[][][] blocks, int x, int y, int z) {
        // Faces with NONE direction are always rendered (internal faces like stair risers)
        if (face.direction == FaceDirection.NONE) {
            return true;
        }

        // Check the adjacent block in the face's direction
        int adjX = x;
        int adjY = y;
        int adjZ = z;

        switch (face.direction) {
            case UP:
                adjY++;
                break;
            case DOWN:
                adjY--;
                break;
            case NORTH:
                adjZ++;
                break;
            case SOUTH:
                adjZ--;
                break;
            case EAST:
                adjX++;
                break;
            case WEST:
                adjX--;
                break;
            case NONE:
                return true; // Always render faces with NONE direction
        }

        // Render the face if adjacent block is air or outside chunk bounds
        return isAirOrTransparent(blocks, adjX, adjY, adjZ);
    }

    /**
     * Gets the appropriate texture for a face based on its texture key
     */
    private static String getTextureForFace(Block block, String textureKey) {
        switch (textureKey) {
            case "top":
                return block.getTopTex();
            case "bottom":
                return block.getBottomTex();
            case "side":
                return block.getSideTex();
            default:
                return block.getSideTex();
        }
    }

    /**
     * Adds a face to the mesh data structures
     */
    private static void addFace(
        String tex,
        Map<String, List<Vector3f>> p,
        Map<String, List<Vector3f>> n,
        Map<String, List<Vector2f>> uvCoords,
        Map<String, List<Integer>> i,
        Map<String, Integer> o,
        Vector3f[] vertices,
        Vector3f normal,
        Vector2f[] uvs
    ) {
        p.computeIfAbsent(tex, k -> new ArrayList<>());
        n.computeIfAbsent(tex, k -> new ArrayList<>());
        uvCoords.computeIfAbsent(tex, k -> new ArrayList<>());
        i.computeIfAbsent(tex, k -> new ArrayList<>());
        o.putIfAbsent(tex, 0);

        int vertexOffset = o.get(tex);

        // Add vertices, normals, and UVs
        Collections.addAll(p.get(tex), vertices);
        for (int k = 0; k < 4; k++) {
            n.get(tex).add(normal);
            uvCoords.get(tex).add(uvs[k]);
        }

        // Add indices for two triangles (quad)
        i.get(tex).add(vertexOffset);
        i.get(tex).add(vertexOffset + 1);
        i.get(tex).add(vertexOffset + 2);
        i.get(tex).add(vertexOffset);
        i.get(tex).add(vertexOffset + 2);
        i.get(tex).add(vertexOffset + 3);

        o.put(tex, vertexOffset + 4);
    }

    /**
     * Checks if a position is air or transparent
     */
    private static boolean isAirOrTransparent(Block[][][] blocks, int x, int y, int z) {
        // Out of bounds = air
        if (x < 0 || y < 0 || z < 0 || x >= Chunk.SIZE || y >= Chunk.SIZE || z >= Chunk.SIZE) {
            return true;
        }

        Block block = blocks[x][y][z];

        // Null = air
        if (block == null) {
            return true;
        }

        // Non-solid blocks (like fences) are transparent for occlusion purposes
        return !block.isFull();
    }
}
