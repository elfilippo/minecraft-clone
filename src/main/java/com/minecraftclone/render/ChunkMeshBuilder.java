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
import java.util.List;

public final class ChunkMeshBuilder {

    /**
     * builds a single mesh for the chunk using greedy meshing for full cube blocks
     * and per-face fallback for non-cube blocks (stairs, slabs, fences).
     * fetches neighbor arrays from world for cross-chunk face culling.
     * used for synchronous rebuilds (block placing/breaking).
     * @param blocks 3d array of blocks in chunk
     * @param world
     * @param chunkX
     * @param chunkY
     * @param chunkZ
     * @return single merged mesh using atlas UVs
     */
    public static Mesh build(Block[][][] blocks, World world, int chunkX, int chunkY, int chunkZ) {
        Block[][][] neighborUp = getChunkBlocks(world, chunkX, chunkY + 1, chunkZ);
        Block[][][] neighborDown = getChunkBlocks(world, chunkX, chunkY - 1, chunkZ);
        Block[][][] neighborNorth = getChunkBlocks(world, chunkX, chunkY, chunkZ + 1);
        Block[][][] neighborSouth = getChunkBlocks(world, chunkX, chunkY, chunkZ - 1);
        Block[][][] neighborEast = getChunkBlocks(world, chunkX + 1, chunkY, chunkZ);
        Block[][][] neighborWest = getChunkBlocks(world, chunkX - 1, chunkY, chunkZ);

        return build(blocks, neighborUp, neighborDown, neighborNorth, neighborSouth, neighborEast, neighborWest);
    }

    /**
     * builds a single mesh for the chunk using pre-fetched neighbor arrays.
     * used by ChunkBuildTask on background thread.
     * full cube blocks use greedy meshing to reduce vertex count dramatically.
     * non-cube blocks fall back to per-face building.
     * @param blocks        3d array of blocks in chunk
     * @param neighborUp    ↓ 3d arrays of neighboring chunks, null if unloaded ↓
     * @param neighborDown
     * @param neighborNorth
     * @param neighborSouth
     * @param neighborEast
     * @param neighborWest
     * @return single merged mesh using atlas UVs
     */
    public static Mesh build(
        Block[][][] blocks,
        Block[][][] neighborUp,
        Block[][][] neighborDown,
        Block[][][] neighborNorth,
        Block[][][] neighborSouth,
        Block[][][] neighborEast,
        Block[][][] neighborWest
    ) {
        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        //DOES: greedy meshing pass for full cube blocks — one pass per face direction
        greedyUp(blocks, neighborUp, positions, normals, uvs, indices);
        greedyDown(blocks, neighborDown, positions, normals, uvs, indices);
        greedyNorth(blocks, neighborNorth, positions, normals, uvs, indices);
        greedySouth(blocks, neighborSouth, positions, normals, uvs, indices);
        greedyEast(blocks, neighborEast, positions, normals, uvs, indices);
        greedyWest(blocks, neighborWest, positions, normals, uvs, indices);

        //DOES: per-face fallback for non-cube blocks (stairs, slabs, fences)
        buildNonCube(
            blocks,
            neighborUp,
            neighborDown,
            neighborNorth,
            neighborSouth,
            neighborEast,
            neighborWest,
            positions,
            normals,
            uvs,
            indices
        );

        return assembleMesh(positions, normals, uvs, indices);
    }

    // -------------------------------------------------------------------------
    // GREEDY MESHING — one explicit method per face direction
    // -------------------------------------------------------------------------
    // Each method sweeps a 2d slice perpendicular to its face direction,
    // builds a mask of which cells need a face (and what texture), then
    // greedily merges adjacent same-texture cells into the largest quads possible.
    // Using explicit methods per direction avoids axis-index arithmetic bugs.

    private static final Vector3f NORMAL_UP = new Vector3f(0, 1, 0);
    private static final Vector3f NORMAL_DOWN = new Vector3f(0, -1, 0);
    private static final Vector3f NORMAL_NORTH = new Vector3f(0, 0, 1);
    private static final Vector3f NORMAL_SOUTH = new Vector3f(0, 0, -1);
    private static final Vector3f NORMAL_EAST = new Vector3f(1, 0, 0);
    private static final Vector3f NORMAL_WEST = new Vector3f(-1, 0, 0);

    /**
     * greedy meshing for UP faces (+Y).
     * slices along Y, mask axes are X (i) and Z (j).
     */
    private static void greedyUp(
        Block[][][] blocks,
        Block[][][] neighborUp,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        int S = Chunk.SIZE;
        for (int y = 0; y < S; y++) {
            String[] mask = new String[S * S];
            for (int x = 0; x < S; x++) {
                for (int z = 0; z < S; z++) {
                    Block b = blocks[x][y][z];
                    if (b == null || !b.isFull()) continue;
                    //IS: neighbor is the block directly above
                    int ny = y + 1;
                    boolean exposed =
                        ny >= S
                            ? (neighborUp == null || neighborUp[x][0][z] == null || !neighborUp[x][0][z].isFull())
                            : (blocks[x][ny][z] == null || !blocks[x][ny][z].isFull());
                    if (exposed) mask[x * S + z] = b.getTopTex();
                }
            }
            //IS: face sits on top of y, so face Y = y+1
            greedyMerge(mask, S, y + 1, NORMAL_UP, true, pos, norm, uvs, idx);
        }
    }

    /**
     * greedy meshing for DOWN faces (-Y).
     * slices along Y, mask axes are X (i) and Z (j).
     */
    private static void greedyDown(
        Block[][][] blocks,
        Block[][][] neighborDown,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        int S = Chunk.SIZE;
        for (int y = 0; y < S; y++) {
            String[] mask = new String[S * S];
            for (int x = 0; x < S; x++) {
                for (int z = 0; z < S; z++) {
                    Block b = blocks[x][y][z];
                    if (b == null || !b.isFull()) continue;
                    int ny = y - 1;
                    boolean exposed =
                        ny < 0
                            ? (neighborDown == null ||
                                  neighborDown[x][S - 1][z] == null ||
                                  !neighborDown[x][S - 1][z].isFull())
                            : (blocks[x][ny][z] == null || !blocks[x][ny][z].isFull());
                    if (exposed) mask[x * S + z] = b.getBottomTex();
                }
            }
            //IS: face sits on bottom of y, so face Y = y
            greedyMerge(mask, S, y, NORMAL_DOWN, false, pos, norm, uvs, idx);
        }
    }

    /**
     * greedy meshing for NORTH faces (+Z).
     * slices along Z, mask axes are X (i) and Y (j).
     */
    private static void greedyNorth(
        Block[][][] blocks,
        Block[][][] neighborNorth,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        int S = Chunk.SIZE;
        for (int z = 0; z < S; z++) {
            String[] mask = new String[S * S];
            for (int x = 0; x < S; x++) {
                for (int y = 0; y < S; y++) {
                    Block b = blocks[x][y][z];
                    if (b == null || !b.isFull()) continue;
                    int nz = z + 1;
                    boolean exposed =
                        nz >= S
                            ? (neighborNorth == null ||
                                  neighborNorth[x][y][0] == null ||
                                  !neighborNorth[x][y][0].isFull())
                            : (blocks[x][y][nz] == null || !blocks[x][y][nz].isFull());
                    if (exposed) mask[x * S + y] = b.getSideTex();
                }
            }
            //IS: face sits on north side of z, so face Z = z+1
            greedyMergeNorth(mask, S, z + 1, pos, norm, uvs, idx);
        }
    }

    /**
     * greedy meshing for SOUTH faces (-Z).
     * slices along Z, mask axes are X (i) and Y (j).
     */
    private static void greedySouth(
        Block[][][] blocks,
        Block[][][] neighborSouth,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        int S = Chunk.SIZE;
        for (int z = 0; z < S; z++) {
            String[] mask = new String[S * S];
            for (int x = 0; x < S; x++) {
                for (int y = 0; y < S; y++) {
                    Block b = blocks[x][y][z];
                    if (b == null || !b.isFull()) continue;
                    int nz = z - 1;
                    boolean exposed =
                        nz < 0
                            ? (neighborSouth == null ||
                                  neighborSouth[x][y][S - 1] == null ||
                                  !neighborSouth[x][y][S - 1].isFull())
                            : (blocks[x][y][nz] == null || !blocks[x][y][nz].isFull());
                    if (exposed) mask[x * S + y] = b.getSideTex();
                }
            }
            //IS: face sits on south side of z, so face Z = z
            greedyMergeSouth(mask, S, z, pos, norm, uvs, idx);
        }
    }

    /**
     * greedy meshing for EAST faces (+X).
     * slices along X, mask axes are Z (i) and Y (j).
     */
    private static void greedyEast(
        Block[][][] blocks,
        Block[][][] neighborEast,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        int S = Chunk.SIZE;
        for (int x = 0; x < S; x++) {
            String[] mask = new String[S * S];
            for (int z = 0; z < S; z++) {
                for (int y = 0; y < S; y++) {
                    Block b = blocks[x][y][z];
                    if (b == null || !b.isFull()) continue;
                    int nx = x + 1;
                    boolean exposed =
                        nx >= S
                            ? (neighborEast == null || neighborEast[0][y][z] == null || !neighborEast[0][y][z].isFull())
                            : (blocks[nx][y][z] == null || !blocks[nx][y][z].isFull());
                    if (exposed) mask[z * S + y] = b.getSideTex();
                }
            }
            //IS: face sits on east side of x, so face X = x+1
            greedyMergeEast(mask, S, x + 1, pos, norm, uvs, idx);
        }
    }

    /**
     * greedy meshing for WEST faces (-X).
     * slices along X, mask axes are Z (i) and Y (j).
     */
    private static void greedyWest(
        Block[][][] blocks,
        Block[][][] neighborWest,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        int S = Chunk.SIZE;
        for (int x = 0; x < S; x++) {
            String[] mask = new String[S * S];
            for (int z = 0; z < S; z++) {
                for (int y = 0; y < S; y++) {
                    Block b = blocks[x][y][z];
                    if (b == null || !b.isFull()) continue;
                    int nx = x - 1;
                    boolean exposed =
                        nx < 0
                            ? (neighborWest == null ||
                                  neighborWest[S - 1][y][z] == null ||
                                  !neighborWest[S - 1][y][z].isFull())
                            : (blocks[nx][y][z] == null || !blocks[nx][y][z].isFull());
                    if (exposed) mask[z * S + y] = b.getSideTex();
                }
            }
            //IS: face sits on west side of x, so face X = x
            greedyMergeWest(mask, S, x, pos, norm, uvs, idx);
        }
    }

    // -------------------------------------------------------------------------
    // GREEDY MERGE — shared merge logic, direction-specific quad emit
    // -------------------------------------------------------------------------

    /**
     * shared greedy merge pass for UP and DOWN faces.
     * mask is indexed [x * S + z]. i = x axis, j = z axis.
     * @param faceY  the Y coordinate of the face plane
     * @param up     true for UP face, false for DOWN face
     */
    private static void greedyMerge(
        String[] mask,
        int S,
        int faceY,
        Vector3f normal,
        boolean up,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        boolean[] used = new boolean[S * S];
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S; j++) {
                int index = i * S + j;
                if (used[index] || mask[index] == null) continue;
                String tex = mask[index];

                //DOES: expand width along i (X axis)
                int width = 1;
                while (i + width < S && !used[(i + width) * S + j] && tex.equals(mask[(i + width) * S + j])) width++;

                //DOES: expand height along j (Z axis)
                int height = 1;
                outer: while (j + height < S) {
                    for (int k = i; k < i + width; k++) {
                        if (used[k * S + j + height] || !tex.equals(mask[k * S + j + height])) break outer;
                    }
                    height++;
                }

                //DOES: mark used
                for (int di = 0; di < width; di++) for (int dj = 0; dj < height; dj++) used[(i + di) * S + j + dj] =
                    true;

                //IS: quad corners — i=X, j=Z, faceY=Y
                float x0 = i,
                    x1 = i + width;
                float z0 = j,
                    z1 = j + height;
                float y = faceY;

                Vector3f[] verts;
                if (up) {
                    //INFO: counter-clockwise when viewed from above
                    verts = new Vector3f[] {
                        new Vector3f(x0, y, z0),
                        new Vector3f(x0, y, z1),
                        new Vector3f(x1, y, z1),
                        new Vector3f(x1, y, z0),
                    };
                } else {
                    verts = new Vector3f[] {
                        new Vector3f(x0, y, z0),
                        new Vector3f(x1, y, z0),
                        new Vector3f(x1, y, z1),
                        new Vector3f(x0, y, z1),
                    };
                }

                //INFO: width=X extent, height=Z extent for horizontal faces
                addQuad(pos, norm, uvs, idx, verts, normal, atlasUVsHorizontal(tex, width, height));
            }
        }
    }

    /**
     * greedy merge for NORTH faces (+Z). mask indexed [x * S + y]. i=X, j=Y.
     * @param faceZ the Z coordinate of the face plane (z+1 of the block)
     */
    private static void greedyMergeNorth(
        String[] mask,
        int S,
        int faceZ,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        boolean[] used = new boolean[S * S];
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S; j++) {
                int index = i * S + j;
                if (used[index] || mask[index] == null) continue;
                String tex = mask[index];

                int width = 1;
                while (i + width < S && !used[(i + width) * S + j] && tex.equals(mask[(i + width) * S + j])) width++;

                int height = 1;
                outer: while (j + height < S) {
                    for (int k = i; k < i + width; k++) {
                        if (used[k * S + j + height] || !tex.equals(mask[k * S + j + height])) break outer;
                    }
                    height++;
                }

                for (int di = 0; di < width; di++) for (int dj = 0; dj < height; dj++) used[(i + di) * S + j + dj] =
                    true;

                //IS: i=X, j=Y, face at Z=faceZ
                float x0 = i,
                    x1 = i + width;
                float y0 = j,
                    y1 = j + height;
                float z = faceZ;

                //INFO: counter-clockwise when viewed from +Z side
                Vector3f[] verts = new Vector3f[] {
                    new Vector3f(x0, y0, z),
                    new Vector3f(x1, y0, z),
                    new Vector3f(x1, y1, z),
                    new Vector3f(x0, y1, z),
                };

                //INFO: width=X extent, height=Y extent for north/south faces
                addQuad(pos, norm, uvs, idx, verts, NORMAL_NORTH, atlasUVsVertical(tex, width, height));
            }
        }
    }

    /**
     * greedy merge for SOUTH faces (-Z). mask indexed [x * S + y]. i=X, j=Y.
     * @param faceZ the Z coordinate of the face plane (z of the block)
     */
    private static void greedyMergeSouth(
        String[] mask,
        int S,
        int faceZ,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        boolean[] used = new boolean[S * S];
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S; j++) {
                int index = i * S + j;
                if (used[index] || mask[index] == null) continue;
                String tex = mask[index];

                int width = 1;
                while (i + width < S && !used[(i + width) * S + j] && tex.equals(mask[(i + width) * S + j])) width++;

                int height = 1;
                outer: while (j + height < S) {
                    for (int k = i; k < i + width; k++) {
                        if (used[k * S + j + height] || !tex.equals(mask[k * S + j + height])) break outer;
                    }
                    height++;
                }

                for (int di = 0; di < width; di++) for (int dj = 0; dj < height; dj++) used[(i + di) * S + j + dj] =
                    true;

                //IS: i=X, j=Y, face at Z=faceZ
                float x0 = i,
                    x1 = i + width;
                float y0 = j,
                    y1 = j + height;
                float z = faceZ;

                //INFO: counter-clockwise when viewed from -Z side (reversed X)
                Vector3f[] verts = new Vector3f[] {
                    new Vector3f(x1, y0, z),
                    new Vector3f(x0, y0, z),
                    new Vector3f(x0, y1, z),
                    new Vector3f(x1, y1, z),
                };

                //INFO: width=X extent, height=Y extent for north/south faces
                addQuad(pos, norm, uvs, idx, verts, NORMAL_SOUTH, atlasUVsVertical(tex, width, height));
            }
        }
    }

    /**
     * greedy merge for EAST faces (+X). mask indexed [z * S + y]. i=Z, j=Y.
     * @param faceX the X coordinate of the face plane (x+1 of the block)
     */
    private static void greedyMergeEast(
        String[] mask,
        int S,
        int faceX,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        boolean[] used = new boolean[S * S];
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S; j++) {
                int index = i * S + j;
                if (used[index] || mask[index] == null) continue;
                String tex = mask[index];

                int width = 1;
                while (i + width < S && !used[(i + width) * S + j] && tex.equals(mask[(i + width) * S + j])) width++;

                int height = 1;
                outer: while (j + height < S) {
                    for (int k = i; k < i + width; k++) {
                        if (used[k * S + j + height] || !tex.equals(mask[k * S + j + height])) break outer;
                    }
                    height++;
                }

                for (int di = 0; di < width; di++) for (int dj = 0; dj < height; dj++) used[(i + di) * S + j + dj] =
                    true;

                //IS: i=Z, j=Y, face at X=faceX
                float z0 = i,
                    z1 = i + width;
                float y0 = j,
                    y1 = j + height;
                float x = faceX;

                //INFO: counter-clockwise when viewed from +X side (reversed Z)
                Vector3f[] verts = new Vector3f[] {
                    new Vector3f(x, y0, z1),
                    new Vector3f(x, y0, z0),
                    new Vector3f(x, y1, z0),
                    new Vector3f(x, y1, z1),
                };

                //INFO: width=Z extent, height=Y extent for east/west faces
                addQuad(pos, norm, uvs, idx, verts, NORMAL_EAST, atlasUVsVertical(tex, width, height));
            }
        }
    }

    /**
     * greedy merge for WEST faces (-X). mask indexed [z * S + y]. i=Z, j=Y.
     * @param faceX the X coordinate of the face plane (x of the block)
     */
    private static void greedyMergeWest(
        String[] mask,
        int S,
        int faceX,
        List<Vector3f> pos,
        List<Vector3f> norm,
        List<Vector2f> uvs,
        List<Integer> idx
    ) {
        boolean[] used = new boolean[S * S];
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S; j++) {
                int index = i * S + j;
                if (used[index] || mask[index] == null) continue;
                String tex = mask[index];

                int width = 1;
                while (i + width < S && !used[(i + width) * S + j] && tex.equals(mask[(i + width) * S + j])) width++;

                int height = 1;
                outer: while (j + height < S) {
                    for (int k = i; k < i + width; k++) {
                        if (used[k * S + j + height] || !tex.equals(mask[k * S + j + height])) break outer;
                    }
                    height++;
                }

                for (int di = 0; di < width; di++) for (int dj = 0; dj < height; dj++) used[(i + di) * S + j + dj] =
                    true;

                //IS: i=Z, j=Y, face at X=faceX
                float z0 = i,
                    z1 = i + width;
                float y0 = j,
                    y1 = j + height;
                float x = faceX;

                //INFO: counter-clockwise when viewed from -X side
                Vector3f[] verts = new Vector3f[] {
                    new Vector3f(x, y0, z0),
                    new Vector3f(x, y0, z1),
                    new Vector3f(x, y1, z1),
                    new Vector3f(x, y1, z0),
                };

                //INFO: width=Z extent, height=Y extent for east/west faces
                addQuad(pos, norm, uvs, idx, verts, NORMAL_WEST, atlasUVsVertical(tex, width, height));
            }
        }
    }

    // -------------------------------------------------------------------------
    // NON-CUBE FALLBACK
    // -------------------------------------------------------------------------

    /**
     * per-face pass for non-cube blocks (stairs, slabs, fences).
     * uses face data from MeshLibrary but remaps UVs to atlas space.
     */
    private static void buildNonCube(
        Block[][][] blocks,
        Block[][][] neighborUp,
        Block[][][] neighborDown,
        Block[][][] neighborNorth,
        Block[][][] neighborSouth,
        Block[][][] neighborEast,
        Block[][][] neighborWest,
        List<Vector3f> positions,
        List<Vector3f> normals,
        List<Vector2f> uvs,
        List<Integer> indices
    ) {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                for (int z = 0; z < Chunk.SIZE; z++) {
                    Block block = blocks[x][y][z];

                    //CASE: skip null and full cube blocks (handled by greedy pass)
                    if (block == null || block.isFull()) continue;

                    BlockGeometry geometry = block.getGeometry();

                    for (Face face : geometry.getFaces()) {
                        if (
                            !shouldRenderFace(
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
                            continue;
                        }

                        String texName = resolveTexture(block, face.textureKey);

                        //IS: face vertices offset to block's local position in chunk
                        Vector3f[] worldVerts = new Vector3f[4];
                        for (int i = 0; i < 4; i++) {
                            worldVerts[i] = face.vertices[i].add(x, y, z);
                        }

                        //DOES: remap each face UV from local (0-1) space to atlas UV space
                        Vector2f[] faceUVs = new Vector2f[4];
                        for (int i = 0; i < 4; i++) {
                            faceUVs[i] = BlockAtlas.remap(texName, face.uvs[i].x, face.uvs[i].y);
                        }

                        addQuad(positions, normals, uvs, indices, worldVerts, face.normal, faceUVs);
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // SHARED UTILITIES
    // -------------------------------------------------------------------------

    /**
     * tiled atlas UVs for horizontal faces (UP/DOWN).
     * tiles the texture across the quad by passing width/height as UV extents.
     * BlockAtlas.remap() takes the fractional part so UVs tile within the tile's atlas region.
     * @param texName texture name
     * @param uSize   quad width in blocks — texture tiles this many times in U
     * @param vSize   quad depth in blocks — texture tiles this many times in V
     */
    private static Vector2f[] atlasUVsHorizontal(String texName, float uSize, float vSize) {
        return new Vector2f[] {
            BlockAtlas.remap(texName, 0, 0),
            BlockAtlas.remap(texName, uSize, 0),
            BlockAtlas.remap(texName, uSize, vSize),
            BlockAtlas.remap(texName, 0, vSize),
        };
    }

    /**
     * tiled atlas UVs for vertical faces (NORTH/SOUTH/EAST/WEST).
     * V is flipped so textures appear right-side up on walls —
     * world Y increases upward but atlas V=0 is the top of the texture.
     * @param texName texture name
     * @param uSize   quad width in blocks — texture tiles this many times in U
     * @param vSize   quad height in blocks — texture tiles this many times in V
     */
    private static Vector2f[] atlasUVsVertical(String texName, float uSize, float vSize) {
        return new Vector2f[] {
            BlockAtlas.remap(texName, 0, vSize),
            BlockAtlas.remap(texName, uSize, vSize),
            BlockAtlas.remap(texName, uSize, 0),
            BlockAtlas.remap(texName, 0, 0),
        };
    }

    /**
     * adds a quad (4 vertices, 2 triangles) into the mesh accumulator lists.
     */
    private static void addQuad(
        List<Vector3f> positions,
        List<Vector3f> normals,
        List<Vector2f> uvs,
        List<Integer> indices,
        Vector3f[] verts,
        Vector3f normal,
        Vector2f[] quadUVs
    ) {
        int base = positions.size();

        Collections.addAll(positions, verts);
        for (int i = 0; i < 4; i++) normals.add(normal);
        Collections.addAll(uvs, quadUVs);

        //IS: two triangles forming the quad (indices 0,1,2 and 0,2,3)
        indices.add(base);
        indices.add(base + 1);
        indices.add(base + 2);
        indices.add(base);
        indices.add(base + 2);
        indices.add(base + 3);
    }

    /**
     * assembles final JME Mesh from accumulated vertex data lists.
     */
    private static Mesh assembleMesh(
        List<Vector3f> positions,
        List<Vector3f> normals,
        List<Vector2f> uvs,
        List<Integer> indices
    ) {
        Mesh mesh = new Mesh();
        if (positions.isEmpty()) return mesh;

        mesh.setBuffer(
            VertexBuffer.Type.Position,
            3,
            BufferUtils.createFloatBuffer(positions.toArray(new Vector3f[0]))
        );
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normals.toArray(new Vector3f[0])));
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(uvs.toArray(new Vector2f[0])));
        mesh.setBuffer(
            VertexBuffer.Type.Index,
            3,
            BufferUtils.createIntBuffer(
                indices
                    .stream()
                    .mapToInt(i -> i)
                    .toArray()
            )
        );

        mesh.updateBound();
        return mesh;
    }

    /**
     * determines if a face on a non-cube block should be rendered.
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
        if (face.direction == OcclusionFace.NONE) return true;

        int adjX = x,
            adjY = y,
            adjZ = z;
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
            default:
                return true;
        }

        return isAirOrTransparent(
            blocks,
            neighborUp,
            neighborDown,
            neighborNorth,
            neighborSouth,
            neighborEast,
            neighborWest,
            adjX,
            adjY,
            adjZ
        );
    }

    /**
     * checks if a position is air or a non-full block.
     * handles cross-chunk lookups via neighbor arrays.
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
        int S = Chunk.SIZE;
        if (x >= 0 && y >= 0 && z >= 0 && x < S && y < S && z < S) {
            Block b = blocks[x][y][z];
            return b == null || !b.isFull();
        }

        Block[][][] neighbor;
        int nx = x,
            ny = y,
            nz = z;

        if (y >= S) {
            neighbor = neighborUp;
            ny = 0;
        } else if (y < 0) {
            neighbor = neighborDown;
            ny = S - 1;
        } else if (z >= S) {
            neighbor = neighborNorth;
            nz = 0;
        } else if (z < 0) {
            neighbor = neighborSouth;
            nz = S - 1;
        } else if (x >= S) {
            neighbor = neighborEast;
            nx = 0;
        } else {
            neighbor = neighborWest;
            nx = S - 1;
        }

        if (neighbor == null) return true;

        Block b = neighbor[nx][ny][nz];
        return b == null || !b.isFull();
    }

    /**
     * resolves a MeshLibrary textureKey ("top", "bottom", "side") to the actual texture name.
     */
    private static String resolveTexture(Block block, String textureKey) {
        switch (textureKey) {
            case "top":
                return block.getTopTex();
            case "bottom":
                return block.getBottomTex();
            default:
                return block.getSideTex();
        }
    }

    /**
     * gets the blocks array of a chunk at given chunk coordinates.
     * returns null if the chunk is not loaded.
     */
    private static Block[][][] getChunkBlocks(World world, int chunkX, int chunkY, int chunkZ) {
        com.minecraftclone.world.chunks.Chunk chunk = world.getChunk(new ChunkPos(chunkX, chunkY, chunkZ));
        return chunk != null ? chunk.getBlocks() : null;
    }
}
