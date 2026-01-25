package com.minecraftclone.world;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.minecraftclone.block.Block;
import java.util.ArrayList;
import java.util.List;

public final class ChunkMeshBuilder {

    public static Mesh build(Block[][][] blocks) {
        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int idx = 0;

        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                for (int z = 0; z < Chunk.SIZE; z++) {
                    Block block = blocks[x][y][z];
                    if (block == null || !block.isSolid()) continue;

                    if (isAir(blocks, x, y + 1, z)) idx = topFace(positions, normals, uvs, indices, idx, x, y, z);

                    if (isAir(blocks, x, y - 1, z)) idx = bottomFace(positions, normals, uvs, indices, idx, x, y, z);

                    if (isAir(blocks, x, y, z + 1)) idx = northFace(positions, normals, uvs, indices, idx, x, y, z);

                    if (isAir(blocks, x, y, z - 1)) idx = southFace(positions, normals, uvs, indices, idx, x, y, z);

                    if (isAir(blocks, x + 1, y, z)) idx = eastFace(positions, normals, uvs, indices, idx, x, y, z);

                    if (isAir(blocks, x - 1, y, z)) idx = westFace(positions, normals, uvs, indices, idx, x, y, z);
                }
            }
        }

        Mesh mesh = new Mesh();
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

    private static boolean isAir(Block[][][] blocks, int x, int y, int z) {
        return (
            x < 0 || y < 0 || z < 0 || x >= Chunk.SIZE || y >= Chunk.SIZE || z >= Chunk.SIZE || blocks[x][y][z] == null
        );
    }

    // =========================
    // FACE DEFINITIONS (CCW)
    // =========================

    // +Y
    private static int topFace(
        List<Vector3f> p,
        List<Vector3f> n,
        List<Vector2f> uv,
        List<Integer> i,
        int idx,
        int x,
        int y,
        int z
    ) {
        p.add(new Vector3f(x, y + 1, z));
        p.add(new Vector3f(x, y + 1, z + 1));
        p.add(new Vector3f(x + 1, y + 1, z + 1));
        p.add(new Vector3f(x + 1, y + 1, z));

        repeat(n, Vector3f.UNIT_Y);
        addUV(uv);
        addIdx(i, idx);
        return idx + 4;
    }

    // -Y
    private static int bottomFace(
        List<Vector3f> p,
        List<Vector3f> n,
        List<Vector2f> uv,
        List<Integer> i,
        int idx,
        int x,
        int y,
        int z
    ) {
        p.add(new Vector3f(x, y, z));
        p.add(new Vector3f(x + 1, y, z));
        p.add(new Vector3f(x + 1, y, z + 1));
        p.add(new Vector3f(x, y, z + 1));

        repeat(n, Vector3f.UNIT_Y.negate());
        addUV(uv);
        addIdx(i, idx);
        return idx + 4;
    }

    // +Z
    private static int northFace(
        List<Vector3f> p,
        List<Vector3f> n,
        List<Vector2f> uv,
        List<Integer> i,
        int idx,
        int x,
        int y,
        int z
    ) {
        p.add(new Vector3f(x, y, z + 1));
        p.add(new Vector3f(x + 1, y, z + 1));
        p.add(new Vector3f(x + 1, y + 1, z + 1));
        p.add(new Vector3f(x, y + 1, z + 1));

        repeat(n, Vector3f.UNIT_Z);
        addUV(uv);
        addIdx(i, idx);
        return idx + 4;
    }

    // -Z
    private static int southFace(
        List<Vector3f> p,
        List<Vector3f> n,
        List<Vector2f> uv,
        List<Integer> i,
        int idx,
        int x,
        int y,
        int z
    ) {
        p.add(new Vector3f(x + 1, y, z));
        p.add(new Vector3f(x, y, z));
        p.add(new Vector3f(x, y + 1, z));
        p.add(new Vector3f(x + 1, y + 1, z));

        repeat(n, Vector3f.UNIT_Z.negate());
        addUV(uv);
        addIdx(i, idx);
        return idx + 4;
    }

    // +X
    private static int eastFace(
        List<Vector3f> p,
        List<Vector3f> n,
        List<Vector2f> uv,
        List<Integer> i,
        int idx,
        int x,
        int y,
        int z
    ) {
        p.add(new Vector3f(x + 1, y, z + 1));
        p.add(new Vector3f(x + 1, y, z));
        p.add(new Vector3f(x + 1, y + 1, z));
        p.add(new Vector3f(x + 1, y + 1, z + 1));

        repeat(n, Vector3f.UNIT_X);
        addUV(uv);
        addIdx(i, idx);
        return idx + 4;
    }

    // -X
    private static int westFace(
        List<Vector3f> p,
        List<Vector3f> n,
        List<Vector2f> uv,
        List<Integer> i,
        int idx,
        int x,
        int y,
        int z
    ) {
        p.add(new Vector3f(x, y, z));
        p.add(new Vector3f(x, y, z + 1));
        p.add(new Vector3f(x, y + 1, z + 1));
        p.add(new Vector3f(x, y + 1, z));

        repeat(n, Vector3f.UNIT_X.negate());
        addUV(uv);
        addIdx(i, idx);
        return idx + 4;
    }

    // =========================
    // HELPERS
    // =========================

    private static void repeat(List<Vector3f> list, Vector3f v) {
        for (int i = 0; i < 4; i++) list.add(v);
    }

    private static void addUV(List<Vector2f> uv) {
        uv.add(new Vector2f(0, 0));
        uv.add(new Vector2f(1, 0));
        uv.add(new Vector2f(1, 1));
        uv.add(new Vector2f(0, 1));
    }

    private static void addIdx(List<Integer> i, int idx) {
        i.add(idx);
        i.add(idx + 1);
        i.add(idx + 2);
        i.add(idx);
        i.add(idx + 2);
        i.add(idx + 3);
    }
}
