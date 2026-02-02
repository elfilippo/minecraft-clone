package com.minecraftclone.render;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.minecraftclone.block.Block;
import com.minecraftclone.world.Chunk;
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
                    if (b == null || !b.isSolid()) continue;

                    if (isAir(blocks, x, y + 1, z)) addFace(
                        b.top(),
                        pos,
                        norm,
                        uv,
                        idx,
                        offset,
                        quad(
                            new Vector3f(x, y + 1, z),
                            new Vector3f(x, y + 1, z + 1),
                            new Vector3f(x + 1, y + 1, z + 1),
                            new Vector3f(x + 1, y + 1, z)
                        ),
                        Vector3f.UNIT_Y
                    );

                    if (isAir(blocks, x, y - 1, z)) addFace(
                        b.bottom(),
                        pos,
                        norm,
                        uv,
                        idx,
                        offset,
                        quad(new Vector3f(x, y, z), new Vector3f(x + 1, y, z), new Vector3f(x + 1, y, z + 1), new Vector3f(x, y, z + 1)),
                        Vector3f.UNIT_Y.negate()
                    );

                    if (isAir(blocks, x, y, z + 1)) addFace(
                        b.side(),
                        pos,
                        norm,
                        uv,
                        idx,
                        offset,
                        quad(
                            new Vector3f(x, y, z + 1),
                            new Vector3f(x + 1, y, z + 1),
                            new Vector3f(x + 1, y + 1, z + 1),
                            new Vector3f(x, y + 1, z + 1)
                        ),
                        Vector3f.UNIT_Z
                    );

                    if (isAir(blocks, x, y, z - 1)) addFace(
                        b.side(),
                        pos,
                        norm,
                        uv,
                        idx,
                        offset,
                        quad(new Vector3f(x + 1, y, z), new Vector3f(x, y, z), new Vector3f(x, y + 1, z), new Vector3f(x + 1, y + 1, z)),
                        Vector3f.UNIT_Z.negate()
                    );

                    if (isAir(blocks, x + 1, y, z)) addFace(
                        b.side(),
                        pos,
                        norm,
                        uv,
                        idx,
                        offset,
                        quad(
                            new Vector3f(x + 1, y, z + 1),
                            new Vector3f(x + 1, y, z),
                            new Vector3f(x + 1, y + 1, z),
                            new Vector3f(x + 1, y + 1, z + 1)
                        ),
                        Vector3f.UNIT_X
                    );

                    if (isAir(blocks, x - 1, y, z)) addFace(
                        b.side(),
                        pos,
                        norm,
                        uv,
                        idx,
                        offset,
                        quad(new Vector3f(x, y, z), new Vector3f(x, y, z + 1), new Vector3f(x, y + 1, z + 1), new Vector3f(x, y + 1, z)),
                        Vector3f.UNIT_X.negate()
                    );
                }
            }
        }

        Map<String, Mesh> meshes = new HashMap<>();

        for (String tex : pos.keySet()) {
            Mesh m = new Mesh();
            m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(pos.get(tex).toArray(new Vector3f[0])));
            m.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(norm.get(tex).toArray(new Vector3f[0])));
            m.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(uv.get(tex).toArray(new Vector2f[0])));
            m.setBuffer(
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
            m.updateBound();
            meshes.put(tex, m);
        }

        return meshes;
    }

    private static Vector3f[] quad(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
        return new Vector3f[] { a, b, c, d };
    }

    private static void addFace(
        String tex,
        Map<String, List<Vector3f>> p,
        Map<String, List<Vector3f>> n,
        Map<String, List<Vector2f>> uv,
        Map<String, List<Integer>> i,
        Map<String, Integer> o,
        Vector3f[] q,
        Vector3f normal
    ) {
        p.computeIfAbsent(tex, k -> new ArrayList<>());
        n.computeIfAbsent(tex, k -> new ArrayList<>());
        uv.computeIfAbsent(tex, k -> new ArrayList<>());
        i.computeIfAbsent(tex, k -> new ArrayList<>());
        o.putIfAbsent(tex, 0);

        int idx = o.get(tex);

        Collections.addAll(p.get(tex), q);
        for (int k = 0; k < 4; k++) {
            n.get(tex).add(normal);
            uv.get(tex).add(new Vector2f(k == 1 || k == 2 ? 1 : 0, k >= 2 ? 1 : 0));
        }

        i.get(tex).add(idx);
        i.get(tex).add(idx + 1);
        i.get(tex).add(idx + 2);
        i.get(tex).add(idx);
        i.get(tex).add(idx + 2);
        i.get(tex).add(idx + 3);

        o.put(tex, idx + 4);
    }

    private static boolean isAir(Block[][][] blocks, int x, int y, int z) {
        return (x < 0 || y < 0 || z < 0 || x >= Chunk.SIZE || y >= Chunk.SIZE || z >= Chunk.SIZE || blocks[x][y][z] == null);
    }
}
