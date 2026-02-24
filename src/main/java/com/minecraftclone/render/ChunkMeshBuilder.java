package com.minecraftclone.render;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.minecraftclone.block.Block;
import com.minecraftclone.block.MeshLibrary.BlockGeometry;
import com.minecraftclone.block.MeshLibrary.Face;
import com.minecraftclone.block.MeshLibrary.OcclusionFace;
import com.minecraftclone.world.World;
import com.minecraftclone.world.chunks.Chunk;
import com.minecraftclone.world.chunks.ChunkPos;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ChunkMeshBuilder {

    /**
     * generates map of meshes that each use a certain texture in a chunk
     * @param blocks 3d array of blocks in chunk
     * @return map of meshes with textures as keys
     */
    public static Map<String, Mesh> build(Block[][][] blocks, World world, int chunkX, int chunkY, int chunkZ) {
        //IS: 3d block arrays for neighboring chunks for cross-chunk culling
        Block[][][] neighborUp = getChunkBlocks(world, chunkX, chunkY + 1, chunkZ);
        Block[][][] neighborDown = getChunkBlocks(world, chunkX, chunkY - 1, chunkZ);
        Block[][][] neighborNorth = getChunkBlocks(world, chunkX, chunkY, chunkZ + 1);
        Block[][][] neighborSouth = getChunkBlocks(world, chunkX, chunkY, chunkZ - 1);
        Block[][][] neighborEast = getChunkBlocks(world, chunkX + 1, chunkY, chunkZ);
        Block[][][] neighborWest = getChunkBlocks(world, chunkX - 1, chunkY, chunkZ);

        //IS: map storing vertex positions
        Map<String, List<Vector3f>> pos = new HashMap<>();

        //IS: map storing vertex normals (facing directions)
        Map<String, List<Vector3f>> norm = new HashMap<>();

        //IS: map storing texture coordinates (which part to display on which vertex)
        Map<String, List<Vector2f>> uv = new HashMap<>();

        //IS: indices, which vertices form each triangle
        Map<String, List<Integer>> index = new HashMap<>();

        //IS: used when building vertices
        //INFO: local (block) coords are translated to chunk coords by adding offset
        Map<String, Integer> offset = new HashMap<>();

        //DOES: iterate through all blocks in chunk
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                for (int z = 0; z < Chunk.SIZE; z++) {
                    //DOES: save current block as block
                    Block block = blocks[x][y][z];
                    if (block == null) continue;

                    //DOES: get geometry for block
                    BlockGeometry geometry = block.getGeometry();

                    //DOES: add each face from the geometry
                    for (Face face : geometry.getFaces()) {
                        //DOES: check if face should be rendered (occlusion culling)
                        if (
                            shouldRenderFace(
                                face,
                                blocks,
                                neighborUp,
                                neighborDown,
                                neighborNorth,
                                neighborSouth,
                                neighborEast,
                                neighborWest,
                                x,
                                y,
                                z
                            )
                        ) {
                            //DOES: get the texture for current face based on its texture key
                            String texture = getTextureForFace(block, face.textureKey);

                            //DOES: offset the face vertices to block's position
                            Vector3f[] worldVertices = new Vector3f[4];
                            for (int i = 0; i < 4; i++) {
                                worldVertices[i] = face.vertices[i].add(x, y, z);
                            }

                            addFace(texture, pos, norm, uv, index, offset, worldVertices, face.normal, face.uvs);
                        }
                    }
                }
            }
        }

        Map<String, Mesh> meshes = new HashMap<>();

        //DOES: create and set up meshes for all textures in chunk
        //DOES: iterate through each texture in the map of positions of textures
        for (String texture : pos.keySet()) {
            //DOES: create mesh for current texture
            var mesh = new Mesh();

            //DOES: set position buffer
            mesh.setBuffer(
                VertexBuffer.Type.Position,
                3,
                BufferUtils.createFloatBuffer(pos.get(texture).toArray(new Vector3f[0]))
            );

            //DOES: set normal buffer (face directions)
            mesh.setBuffer(
                VertexBuffer.Type.Normal,
                3,
                BufferUtils.createFloatBuffer(norm.get(texture).toArray(new Vector3f[0]))
            );

            //DOES: set local texture coordinate (uv) buffer
            mesh.setBuffer(
                VertexBuffer.Type.TexCoord,
                2,
                BufferUtils.createFloatBuffer(uv.get(texture).toArray(new Vector2f[0]))
            );

            //DOES: set index buffer (which indices form each triangle)
            mesh.setBuffer(
                VertexBuffer.Type.Index,
                3,
                BufferUtils.createIntBuffer(
                    //DOES: get List<Integer> from index map and convert to int[] with stream()
                    index
                        .get(texture)
                        .stream()
                        .mapToInt(i -> i)
                        .toArray()
                )
            );

            //DOES: update bounding box of mesh
            mesh.updateBound();

            //DOES: put mesh into meshes map with texture as key
            meshes.put(texture, mesh);
        }
        return meshes;
    }

    /**
     * determines if a face should be rendered based on occlusion
     * checks across chunks as well
     * @param face the face to be rendered
     * @param blocks the 3d array of the current chunk
     * @param neighborUp ↓ 3d arrays of neighboring chunks ↓
     * @param neighborDown
     * @param neighborNorth
     * @param neighborSouth
     * @param neighborEast
     * @param neighborWest
     * @param x ↓ coordinates of the face ↓
     * @param y
     * @param z
     * @return
     */
    private static boolean shouldRenderFace(
        Face face,
        Block[][][] blocks,
        Block[][][] neighborUp,
        Block[][][] neighborDown,
        Block[][][] neighborNorth,
        Block[][][] neighborSouth,
        Block[][][] neighborEast,
        Block[][][] neighborWest,
        int x,
        int y,
        int z
    ) {
        //DOES: return since faces with NONE direction are always rendered
        //NOTE: e.g. faces that never border entire block like stair sides or fence posts
        if (face.direction == OcclusionFace.NONE) {
            return true;
        }

        //DOES: check adjacent block in the face's direction
        int adjFaceX = x;
        int adjFaceY = y;
        int adjFaceZ = z;

        //DOES: determine coordinates for adjacent block in direction
        //NOTE: ex. if block is at 0,0,0 adds 1 to adjFaceY for block above
        switch (face.direction) {
            case UP:
                adjFaceY++;
                break;
            case DOWN:
                adjFaceY--;
                break;
            case NORTH:
                adjFaceZ++;
                break;
            case SOUTH:
                adjFaceZ--;
                break;
            case EAST:
                adjFaceX++;
                break;
            case WEST:
                adjFaceX--;
                break;
            //INFO: will never trigger, but needed for switch statement
            case NONE:
                return true;
        }

        //DOES: render face if adjacent block is air, transparent, or outside chunk
        return isAirOrTransparent(
            blocks,
            neighborUp,
            neighborDown,
            neighborNorth,
            neighborSouth,
            neighborEast,
            neighborWest,
            adjFaceX,
            adjFaceY,
            adjFaceZ
        );
    }

    /**
     * gets the appropriate texture for a face based on its texture key
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
     * adds a face to the mesh data structures
     */
    private static void addFace(
        String texture,
        Map<String, List<Vector3f>> pos,
        Map<String, List<Vector3f>> normalMap,
        Map<String, List<Vector2f>> uvMap,
        Map<String, List<Integer>> index,
        Map<String, Integer> offset,
        Vector3f[] vertices,
        Vector3f normal,
        Vector2f[] uvs
    ) {
        //DOES: ensure lists exist for each texture
        pos.computeIfAbsent(texture, k -> new ArrayList<>());
        normalMap.computeIfAbsent(texture, k -> new ArrayList<>());
        uvMap.computeIfAbsent(texture, k -> new ArrayList<>());
        index.computeIfAbsent(texture, k -> new ArrayList<>());

        //DOES: initialize offset if doesn't exist
        offset.putIfAbsent(texture, 0);

        //DOES: get vertex count for texture (how many vertices have been added already)
        //INFO: this is so the correct vertex positons are referenced
        //NOTE: ex. if grass has 8 vertices, next vertices should be 8-12, etc.
        int vertexOffset = offset.get(texture);

        //DOES: add all vertices to list of pos<string, list<vector3f>> at current texture
        //INFO: Collections.addAll takes all elements of an array and adds them to a collection (e.g. a list)
        Collections.addAll(pos.get(texture), vertices);

        //DOES: add normal to list of normalMap<string, list<vector3f>> in each loop
        //DOES: add uv of each vertex to uvMap<string, list<vector2f>> from uvs array
        //INFO: each vertex has same normal, but different uv coordinates
        for (int k = 0; k < 4; k++) {
            normalMap.get(texture).add(normal);
            uvMap.get(texture).add(uvs[k]);
        }

        //DOES: add indices for two triangles (quad)
        //NOTE: triangle 1 at 0,1,2, triangle 2 at 0,2,3
        index.get(texture).add(vertexOffset);
        index.get(texture).add(vertexOffset + 1);
        index.get(texture).add(vertexOffset + 2);

        index.get(texture).add(vertexOffset);
        index.get(texture).add(vertexOffset + 2);
        index.get(texture).add(vertexOffset + 3);

        //DOES: add offset for next vertex
        offset.put(texture, vertexOffset + 4);
    }

    /**
     * checks if a position is air or transparent
     * @param blocks 3d array of blocks in chunk
     * @param neighborUp ↓ 3d arrays of neighboring chunks ↓
     * @param neighborDown
     * @param neighborNorth
     * @param neighborSouth
     * @param neighborEast
     * @param neighborWest
     * @param x ↓ coordinates of the block ↓
     * @param y
     * @param z
     * @return true if position is air or transparent
     */
    private static boolean isAirOrTransparent(
        Block[][][] blocks,
        Block[][][] neighborUp,
        Block[][][] neighborDown,
        Block[][][] neighborNorth,
        Block[][][] neighborSouth,
        Block[][][] neighborEast,
        Block[][][] neighborWest,
        int x,
        int y,
        int z
    ) {
        //CASE: in chunk bounds -> local blocks array used
        if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE && z < Chunk.SIZE) {
            Block block = blocks[x][y][z];
            return block == null || !block.isFull();
        }

        //CASE: out of bounds
        //DOES: pick the right neighbor array and mirror the coordinates
        Block[][][] neighbor;
        int neighborX = x,
            neighborY = y,
            neighborZ = z;

        if (y >= Chunk.SIZE) {
            neighbor = neighborUp;
            neighborY = 0;
        } else if (y < 0) {
            neighbor = neighborDown;
            neighborY = Chunk.SIZE - 1;
        } else if (z >= Chunk.SIZE) {
            neighbor = neighborNorth;
            neighborZ = 0;
        } else if (z < 0) {
            neighbor = neighborSouth;
            neighborZ = Chunk.SIZE - 1;
        } else if (x >= Chunk.SIZE) {
            neighbor = neighborEast;
            neighborX = 0;
        } else {
            neighbor = neighborWest;
            neighborX = Chunk.SIZE - 1;
        }

        //CASE: null neighbor -> unloaded -> render face
        if (neighbor == null) return true;

        //NOTE: should treat blocks like stairs that do have some full faces otherwise to improve performance
        //INFO: non-full blocks treated as transparent for occlusion purposes
        Block block = neighbor[neighborX][neighborY][neighborZ];
        return block == null || !block.isFull();
    }

    /**
     * gets blocks array of chunk at given chunk pos
     * @param world
     * @param chunkX
     * @param chunkY
     * @param chunkZ
     * @return
     */
    private static Block[][][] getChunkBlocks(World world, int chunkX, int chunkY, int chunkZ) {
        Chunk chunk = world.getChunk(new ChunkPos(chunkX, chunkY, chunkZ));
        return chunk != null ? chunk.getBlocks() : null;
    }
}
