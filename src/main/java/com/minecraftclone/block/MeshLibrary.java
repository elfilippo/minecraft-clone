package com.minecraftclone.block;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * stores all types of block geometries / meshes.
 * each geometry is a set of faces with their vertices, normals, and UV coordinates.
 */
public class MeshLibrary {

    /**
     * Represents a single face (quad) of a block
     */
    public static class Face {

        public final Vector3f[] vertices; //IS: 4 vertices in counter clockwise order
        public final Vector3f normal;

        //IS: 4 uv coordinates (for each vertex)
        //INFO: a uv is a coordinate-based way to say how a texture / 2d image is pinned to the 3d object
        //INFO: u is horizontal, v is vertical, values are from 0 to 1
        //NOTE: ex. texture on the bottom right corner would be u = 0.5, v = 0, u2 = 1, v2 = 0.5
        public final Vector2f[] uvs;

        //NOTE: ex. "top", "bottom", "side"
        public final String textureKey;

        //IS: Which direction this face is facing
        public final OcclusionFace direction;

        public Face(Vector3f[] vertices, Vector3f normal, Vector2f[] uvs, String textureKey, OcclusionFace direction) {
            this.vertices = vertices;
            this.normal = normal;
            this.uvs = uvs;
            this.textureKey = textureKey;
            this.direction = direction;
        }
    }

    /**
     * enum for face directions to check occlusion
     */
    public enum OcclusionFace {
        //INFO: NONE for no occlusion
        UP,
        DOWN,
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NONE,
    }

    /**
     * complete static block geometry saved in list of faces
     */
    public static class BlockGeometry {

        private final List<Face> faces = new ArrayList<>();

        public void addFace(
            Vector3f[] vertices,
            Vector3f normal,
            Vector2f[] uvs,
            String textureKey,
            OcclusionFace direction
        ) {
            faces.add(new Face(vertices, normal, uvs, textureKey, direction));
        }

        public List<Face> getFaces() {
            return faces;
        }
    }

    //DOES: standard uv coordinates for a full face
    private static final Vector2f[] STANDARD_UVS = new Vector2f[] {
        new Vector2f(0, 0),
        new Vector2f(1, 0),
        new Vector2f(1, 1),
        new Vector2f(0, 1),
    };

    /**
     * creates uv coordinates for a partial face
     * @param u1 left u coordinate (0-1)
     * @param v1 bottom v coordinate (0-1)
     * @param u2 right u coordinate (0-1)
     * @param v2 top v coordinate (0-1)
     */
    private static Vector2f[] uvs(float u1, float v1, float u2, float v2) {
        return new Vector2f[] {
            new Vector2f(u1, v1),
            new Vector2f(u2, v1),
            new Vector2f(u2, v2),
            new Vector2f(u1, v2),
        };
    }

    /**
     * creates a horizontal face (parallel to xz plane)
     * @param x1 - first corner x
     * @param z1 - first corner z
     * @param x2 - opposite corner x
     * @param z2 - opposite corner z
     * @param y - height
     * @param up - true for upward facing, false for downward
     */
    private static Vector3f[] horizontalFace(float x1, float z1, float x2, float z2, float y, boolean up) {
        if (up) {
            return new Vector3f[] {
                new Vector3f(x1, y, z1),
                new Vector3f(x1, y, z2),
                new Vector3f(x2, y, z2),
                new Vector3f(x2, y, z1),
            };
        } else {
            return new Vector3f[] {
                new Vector3f(x1, y, z1),
                new Vector3f(x2, y, z1),
                new Vector3f(x2, y, z2),
                new Vector3f(x1, y, z2),
            };
        }
    }

    /**
     * creates a vertical face parallel to xy plane (north/south)
     * @param x1 - first corner x
     * @param y1 - first corner y
     * @param x2 - opposite corner x
     * @param y2 - opposite corner y
     * @param z - z-coordinate
     * @param north - true for north, false for south
     */
    private static Vector3f[] verticalFaceZ(float x1, float y1, float x2, float y2, float z, boolean north) {
        if (north) {
            return new Vector3f[] {
                new Vector3f(x1, y1, z),
                new Vector3f(x2, y1, z),
                new Vector3f(x2, y2, z),
                new Vector3f(x1, y2, z),
            };
        } else {
            return new Vector3f[] {
                new Vector3f(x2, y1, z),
                new Vector3f(x1, y1, z),
                new Vector3f(x1, y2, z),
                new Vector3f(x2, y2, z),
            };
        }
    }

    /**
     * creates a vertical face parallel to yz plane (east/west)
     * @param z1 - first corner z
     * @param y1 - first corner y
     * @param z2 - opposite corner z
     * @param y2 - opposite corner y
     * @param x - x-coordinate
     * @param east - true for east, false for west
     */
    private static Vector3f[] verticalFaceX(float z1, float y1, float z2, float y2, float x, boolean east) {
        if (east) {
            return new Vector3f[] {
                new Vector3f(x, y1, z2),
                new Vector3f(x, y1, z1),
                new Vector3f(x, y2, z1),
                new Vector3f(x, y2, z2),
            };
        } else {
            return new Vector3f[] {
                new Vector3f(x, y1, z1),
                new Vector3f(x, y1, z2),
                new Vector3f(x, y2, z2),
                new Vector3f(x, y2, z1),
            };
        }
    }

    /**
     * adds a rectangular box to geometry (list of faces)
     */
    private static void addBox(
        BlockGeometry geometry,
        float x1,
        float y1,
        float z1,
        float x2,
        float y2,
        float z2,
        String topTex,
        String bottomTex,
        String sideTex,
        OcclusionFace topDir,
        OcclusionFace bottomDir,
        OcclusionFace northDir,
        OcclusionFace southDir,
        OcclusionFace eastDir,
        OcclusionFace westDir
    ) {
        //INFO: Top
        geometry.addFace(horizontalFace(x1, z1, x2, z2, y2, true), Vector3f.UNIT_Y, STANDARD_UVS, topTex, topDir);

        //INFO: Bottom
        geometry.addFace(
            horizontalFace(x1, z1, x2, z2, y1, false),
            Vector3f.UNIT_Y.negate(),
            STANDARD_UVS,
            bottomTex,
            bottomDir
        );

        //INFO: North (+Z)
        geometry.addFace(verticalFaceZ(x1, y1, x2, y2, z2, true), Vector3f.UNIT_Z, STANDARD_UVS, sideTex, northDir);

        //INFO: South (-Z)
        geometry.addFace(
            verticalFaceZ(x1, y1, x2, y2, z1, false),
            Vector3f.UNIT_Z.negate(),
            STANDARD_UVS,
            sideTex,
            southDir
        );

        //INFO: East (+X)
        geometry.addFace(verticalFaceX(z1, y1, z2, y2, x2, true), Vector3f.UNIT_X, STANDARD_UVS, sideTex, eastDir);

        //INFO: West (-X)
        geometry.addFace(
            verticalFaceX(z1, y1, z2, y2, x1, false),
            Vector3f.UNIT_X.negate(),
            STANDARD_UVS,
            sideTex,
            westDir
        );
    }

    /**
     * standard cube block (1x1x1)
     */
    public static final BlockGeometry CUBE = createCube();

    private static BlockGeometry createCube() {
        BlockGeometry cube = new BlockGeometry();
        addBox(
            cube,
            0,
            0,
            0,
            1,
            1,
            1,
            "top",
            "bottom",
            "side",
            OcclusionFace.UP,
            OcclusionFace.DOWN,
            OcclusionFace.NORTH,
            OcclusionFace.SOUTH,
            OcclusionFace.EAST,
            OcclusionFace.WEST
        );
        return cube;
    }

    /**
     * slab (1x0.5x1)
     */
    public static final BlockGeometry SLAB = createSlab();

    private static BlockGeometry createSlab() {
        BlockGeometry slab = new BlockGeometry();

        //INFO: top face always visible, so no occlusion face (half height block)
        slab.addFace(horizontalFace(0, 0, 1, 1, 0.5f, true), Vector3f.UNIT_Y, STANDARD_UVS, "top", OcclusionFace.NONE);

        //INFO: bottom
        slab.addFace(
            horizontalFace(0, 0, 1, 1, 0, false),
            Vector3f.UNIT_Y.negate(),
            STANDARD_UVS,
            "bottom",
            OcclusionFace.DOWN
        );

        //INFO: sides (half height)
        slab.addFace(
            verticalFaceZ(0, 0, 1, 0.5f, 1, true),
            Vector3f.UNIT_Z,
            uvs(0, 0, 1, 0.5f),
            "side",
            OcclusionFace.NORTH
        );
        slab.addFace(
            verticalFaceZ(0, 0, 1, 0.5f, 0, false),
            Vector3f.UNIT_Z.negate(),
            uvs(0, 0, 1, 0.5f),
            "side",
            OcclusionFace.SOUTH
        );
        slab.addFace(
            verticalFaceX(0, 0, 1, 0.5f, 1, true),
            Vector3f.UNIT_X,
            uvs(0, 0, 1, 0.5f),
            "side",
            OcclusionFace.EAST
        );
        slab.addFace(
            verticalFaceX(0, 0, 1, 0.5f, 0, false),
            Vector3f.UNIT_X.negate(),
            uvs(0, 0, 1, 0.5f),
            "side",
            OcclusionFace.WEST
        );
        return slab;
    }

    /**
     * stairs facing north
     */
    public static final BlockGeometry STAIRS_NORTH = createStairsNorth();

    private static BlockGeometry createStairsNorth() {
        BlockGeometry stairs = new BlockGeometry();

        //INFO: bottom step top (front half)
        stairs.addFace(
            horizontalFace(0, 0, 1, 0.5f, 0.5f, true),
            Vector3f.UNIT_Y,
            uvs(0, 0, 1, 0.5f),
            "top",
            OcclusionFace.NONE
        );

        //INFO: top step top (back half)
        stairs.addFace(
            horizontalFace(0, 0.5f, 1, 1, 1, true),
            Vector3f.UNIT_Y,
            uvs(0, 0.5f, 1, 1),
            "top",
            OcclusionFace.UP
        );

        //INFO: bottom (full)
        stairs.addFace(
            horizontalFace(0, 0, 1, 1, 0, false),
            Vector3f.UNIT_Y.negate(),
            STANDARD_UVS,
            "bottom",
            OcclusionFace.DOWN
        );

        //INFO: north face (back, full height)
        stairs.addFace(verticalFaceZ(0, 0, 1, 1, 1, true), Vector3f.UNIT_Z, STANDARD_UVS, "side", OcclusionFace.NORTH);

        //INFO: south face (front, half height)
        stairs.addFace(
            verticalFaceZ(0, 0, 1, 0.5f, 0, false),
            Vector3f.UNIT_Z.negate(),
            uvs(0, 0, 1, 0.5f),
            "side",
            OcclusionFace.SOUTH
        );

        //INFO: upper south face (vertical face between steps)
        stairs.addFace(
            verticalFaceZ(0, 0.5f, 1, 1, 0.5f, false),
            new Vector3f(0, 0, -1),
            uvs(0, 0.5f, 1, 1),
            "side",
            OcclusionFace.NONE
        );

        //INFO: east face (3 parts: bottom front, top back, diagonal connector)
        //TODO: see if this can be simplified & full can be removed as a parameter (renamed & reused as transparent)
        stairs.addFace(
            verticalFaceX(0, 0, 0.5f, 0.5f, 1, true),
            Vector3f.UNIT_X,
            uvs(0.5f, 0, 1, 0.5f),
            "side",
            OcclusionFace.EAST
        );
        stairs.addFace(
            verticalFaceX(0.5f, 0.5f, 1, 1, 1, true),
            Vector3f.UNIT_X,
            uvs(0, 0.5f, 0.5f, 1),
            "side",
            OcclusionFace.EAST
        );
        stairs.addFace(
            verticalFaceX(0.5f, 0, 1, 0.5f, 1, true),
            Vector3f.UNIT_X,
            uvs(0, 0, 0.5f, 0.5f),
            "side",
            OcclusionFace.EAST
        );

        //INFO: west face (3 parts: bottom front, top back, diagonal connector)
        stairs.addFace(
            verticalFaceX(0, 0, 0.5f, 0.5f, 0, false),
            Vector3f.UNIT_X.negate(),
            uvs(0.5f, 0, 1, 0.5f),
            "side",
            OcclusionFace.WEST
        );
        stairs.addFace(
            verticalFaceX(0.5f, 0.5f, 1, 1, 0, false),
            Vector3f.UNIT_X.negate(),
            uvs(0, 0.5f, 0.5f, 1),
            "side",
            OcclusionFace.WEST
        );
        stairs.addFace(
            verticalFaceX(0.5f, 0, 1, 0.5f, 0, false),
            Vector3f.UNIT_X.negate(),
            uvs(0, 0, 0.5f, 0.5f),
            "side",
            OcclusionFace.WEST
        );

        return stairs;
    }

    /**
     * fence post (thin vertical pole)
     */
    public static final BlockGeometry FENCE_POST = createFencePost();

    private static BlockGeometry createFencePost() {
        BlockGeometry fence = new BlockGeometry();
        float thickness = 0.125f;
        float min = 0.5f - thickness;
        float max = 0.5f + thickness;

        addBox(
            fence,
            min,
            0,
            min,
            max,
            1,
            max,
            "top",
            "bottom",
            "side",
            OcclusionFace.UP,
            OcclusionFace.DOWN,
            OcclusionFace.NONE,
            OcclusionFace.NONE,
            OcclusionFace.NONE,
            OcclusionFace.NONE
        );
        return fence;
    }

    /**
     * rotate a block geometry around Y axis
     * @param geometry original geometry
     * @param rotations number of 90-degree rotations (0-3)
     * @return new rotated geometry
     */
    public static BlockGeometry rotateY(BlockGeometry geometry, int rotations) {
        BlockGeometry rotated = new BlockGeometry();

        //DOES: loop rotations through modulus (4 becomes 0 and 6 becomes 2, etc.)
        rotations = rotations % 4;

        //DOES: rotate each face of geometry
        for (Face face : geometry.getFaces()) {
            Vector3f[] newVertices = new Vector3f[4];
            for (int i = 0; i < 4; i++) {
                newVertices[i] = rotateVertexY(face.vertices[i], rotations);
            }

            //DOES: rotate normal and occlusion face
            Vector3f newNormal = rotateVertexY(face.normal, rotations);
            OcclusionFace newDirection = rotateFaceDirection(face.direction, rotations);

            //DOES: add rotated face to new geometry
            rotated.addFace(newVertices, newNormal, face.uvs, face.textureKey, newDirection);
        }

        return rotated;
    }

    /**
     * rotates a vertex around Y axis in 90 degree intervals
     * @param vertex
     * @param rotations
     */
    private static Vector3f rotateVertexY(Vector3f vertex, int rotations) {
        Vector3f result = vertex.clone();

        //DOES: go through each rotation
        for (int i = 0; i < rotations; i++) {
            //DOES: set new x coordinate to old z coordinate -> rotate by 90 degrees
            float newX = result.z;

            //DOES: set new z coordinate to old x coordinate -> rotate by 90 degrees
            float newZ = 1 - result.x;

            //DOES: save new coordinates into result vector
            result = new Vector3f(newX, result.y, newZ);
        }
        return result;
    }

    /**
     * rotates occlusion face in 90 degree intervals
     * @param facingDirection
     * @param rotations
     */
    private static OcclusionFace rotateFaceDirection(OcclusionFace facingDirection, int rotations) {
        //CASE: if rotating doesn't change anything
        if (
            facingDirection == OcclusionFace.UP ||
            facingDirection == OcclusionFace.DOWN ||
            facingDirection == OcclusionFace.NONE
        ) {
            return facingDirection;
        }

        //IS: compass array (clockwise directions)
        OcclusionFace[] directions = {
            OcclusionFace.SOUTH,
            OcclusionFace.EAST,
            OcclusionFace.NORTH,
            OcclusionFace.WEST,
        };

        //IS: index of direction (position in array)
        int index = 0;

        //DOES: go through each rotation, breaks when facingDirection is found & saves it in index
        for (int i = 0; i < 4; i++) {
            if (directions[i] == facingDirection) {
                index = i;
                break;
            }
        }

        //DOES: return rotated direction
        //NOTE: takes modulated index + rotations as an index to get correct item to return from array
        return directions[(index + rotations) % 4];
    }
}
