package com.minecraftclone.block;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores all special types of block geometries.
 * Each geometry is defined as a set of faces with their vertices, normals, and UV coordinates.
 */
public class MeshLibrary {

    /**
     * Represents a single face (quad) of a block
     */
    public static class Face {
        public final Vector3f[] vertices; // 4 vertices in CCW order
        public final Vector3f normal;
        public final Vector2f[] uvs; // 4 UV coordinates
        public final String textureKey; // "top", "bottom", "side"
        public final FaceDirection direction; // Which direction this face is facing

        public Face(Vector3f[] vertices, Vector3f normal, Vector2f[] uvs, String textureKey, FaceDirection direction) {
            this.vertices = vertices;
            this.normal = normal;
            this.uvs = uvs;
            this.textureKey = textureKey;
            this.direction = direction;
        }
    }

    /**
     * Enum for face directions to check occlusion
     */
    public enum FaceDirection {
        UP, DOWN, NORTH, SOUTH, EAST, WEST, NONE
    }

    /**
     * Represents a complete block geometry
     */
    public static class BlockGeometry {
        private final List<Face> faces = new ArrayList<>();

        public void addFace(Vector3f[] vertices, Vector3f normal, Vector2f[] uvs, String textureKey, FaceDirection direction) {
            faces.add(new Face(vertices, normal, uvs, textureKey, direction));
        }

        public List<Face> getFaces() {
            return faces;
        }
    }

    // Standard UV coordinates for a full face
    private static final Vector2f[] STANDARD_UVS = new Vector2f[] {
        new Vector2f(0, 0),
        new Vector2f(1, 0),
        new Vector2f(1, 1),
        new Vector2f(0, 1)
    };

    /**
     * Standard cube block (1x1x1)
     */
    public static final BlockGeometry CUBE = createCube();

    private static BlockGeometry createCube() {
        BlockGeometry cube = new BlockGeometry();

        // Top face (Y+)
        cube.addFace(
            new Vector3f[] {
                new Vector3f(0, 1, 0),
                new Vector3f(0, 1, 1),
                new Vector3f(1, 1, 1),
                new Vector3f(1, 1, 0)
            },
            Vector3f.UNIT_Y,
            STANDARD_UVS,
            "top",
            FaceDirection.UP
        );

        // Bottom face (Y-)
        cube.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 0, 1),
                new Vector3f(0, 0, 1)
            },
            Vector3f.UNIT_Y.negate(),
            STANDARD_UVS,
            "bottom",
            FaceDirection.DOWN
        );

        // North face (Z+)
        cube.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 1),
                new Vector3f(1, 0, 1),
                new Vector3f(1, 1, 1),
                new Vector3f(0, 1, 1)
            },
            Vector3f.UNIT_Z,
            STANDARD_UVS,
            "side",
            FaceDirection.NORTH
        );

        // South face (Z-)
        cube.addFace(
            new Vector3f[] {
                new Vector3f(1, 0, 0),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0)
            },
            Vector3f.UNIT_Z.negate(),
            STANDARD_UVS,
            "side",
            FaceDirection.SOUTH
        );

        // East face (X+)
        cube.addFace(
            new Vector3f[] {
                new Vector3f(1, 0, 1),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 1, 0),
                new Vector3f(1, 1, 1)
            },
            Vector3f.UNIT_X,
            STANDARD_UVS,
            "side",
            FaceDirection.EAST
        );

        // West face (X-)
        cube.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 1),
                new Vector3f(0, 1, 0)
            },
            Vector3f.UNIT_X.negate(),
            STANDARD_UVS,
            "side",
            FaceDirection.WEST
        );

        return cube;
    }

    /**
     * Half slab (1x0.5x1)
     */
    public static final BlockGeometry SLAB = createSlab();

    private static BlockGeometry createSlab() {
        BlockGeometry slab = new BlockGeometry();

        // Top face
        slab.addFace(
            new Vector3f[] {
                new Vector3f(0, 0.5f, 0),
                new Vector3f(0, 0.5f, 1),
                new Vector3f(1, 0.5f, 1),
                new Vector3f(1, 0.5f, 0)
            },
            Vector3f.UNIT_Y,
            STANDARD_UVS,
            "top",
            FaceDirection.UP
        );

        // Bottom face
        slab.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 0, 1),
                new Vector3f(0, 0, 1)
            },
            Vector3f.UNIT_Y.negate(),
            STANDARD_UVS,
            "bottom",
            FaceDirection.DOWN
        );

        // North face (half height)
        slab.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 1),
                new Vector3f(1, 0, 1),
                new Vector3f(1, 0.5f, 1),
                new Vector3f(0, 0.5f, 1)
            },
            Vector3f.UNIT_Z,
            new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(1, 0.5f),
                new Vector2f(0, 0.5f)
            },
            "side",
            FaceDirection.NORTH
        );

        // South face
        slab.addFace(
            new Vector3f[] {
                new Vector3f(1, 0, 0),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0.5f, 0),
                new Vector3f(1, 0.5f, 0)
            },
            Vector3f.UNIT_Z.negate(),
            new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(1, 0.5f),
                new Vector2f(0, 0.5f)
            },
            "side",
            FaceDirection.SOUTH
        );

        // East face
        slab.addFace(
            new Vector3f[] {
                new Vector3f(1, 0, 1),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 0.5f, 0),
                new Vector3f(1, 0.5f, 1)
            },
            Vector3f.UNIT_X,
            new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(1, 0.5f),
                new Vector2f(0, 0.5f)
            },
            "side",
            FaceDirection.EAST
        );

        // West face
        slab.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0, 1),
                new Vector3f(0, 0.5f, 1),
                new Vector3f(0, 0.5f, 0)
            },
            Vector3f.UNIT_X.negate(),
            new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(1, 0.5f),
                new Vector2f(0, 0.5f)
            },
            "side",
            FaceDirection.WEST
        );

        return slab;
    }

    /**
     * Stairs facing north (ascending in +Z direction)
     * Two steps: bottom 0-0.5, top back half 0.5-1
     */
    public static final BlockGeometry STAIRS_NORTH = createStairsNorth();

    private static BlockGeometry createStairsNorth() {
        BlockGeometry stairs = new BlockGeometry();

        // Bottom step (front half, full width)
        // Top of bottom step
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(0, 0.5f, 0),
                new Vector3f(0, 0.5f, 0.5f),
                new Vector3f(1, 0.5f, 0.5f),
                new Vector3f(1, 0.5f, 0)
            },
            Vector3f.UNIT_Y,
            new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(0, 0.5f),
                new Vector2f(1, 0.5f),
                new Vector2f(1, 0)
            },
            "top",
            FaceDirection.NONE // Always visible
        );

        // Top step (back half)
        // Top of top step
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(0, 1, 0.5f),
                new Vector3f(0, 1, 1),
                new Vector3f(1, 1, 1),
                new Vector3f(1, 1, 0.5f)
            },
            Vector3f.UNIT_Y,
            new Vector2f[] {
                new Vector2f(0, 0.5f),
                new Vector2f(0, 1),
                new Vector2f(1, 1),
                new Vector2f(1, 0.5f)
            },
            "top",
            FaceDirection.UP
        );

        // Bottom face (full)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 0, 1),
                new Vector3f(0, 0, 1)
            },
            Vector3f.UNIT_Y.negate(),
            STANDARD_UVS,
            "bottom",
            FaceDirection.DOWN
        );

        // North face (back, full height)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 1),
                new Vector3f(1, 0, 1),
                new Vector3f(1, 1, 1),
                new Vector3f(0, 1, 1)
            },
            Vector3f.UNIT_Z,
            STANDARD_UVS,
            "side",
            FaceDirection.NORTH
        );

        // South face (front, half height - bottom step)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(1, 0, 0),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0.5f, 0),
                new Vector3f(1, 0.5f, 0)
            },
            Vector3f.UNIT_Z.negate(),
            new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(1, 0.5f),
                new Vector2f(0, 0.5f)
            },
            "side",
            FaceDirection.SOUTH
        );

        // Vertical face between steps (step riser) - this is the FRONT face of the upper step
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(1, 0.5f, 0.5f),
                new Vector3f(0, 0.5f, 0.5f),
                new Vector3f(0, 1, 0.5f),
                new Vector3f(1, 1, 0.5f)
            },
            new Vector3f(0, 0, -1),
            new Vector2f[] {
                new Vector2f(0, 0.5f),
                new Vector2f(1, 0.5f),
                new Vector2f(1, 1),
                new Vector2f(0, 1)
            },
            "side",
            FaceDirection.NONE // Always visible
        );

        // East face - Bottom part (front lower section)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(1, 0, 0.5f),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 0.5f, 0),
                new Vector3f(1, 0.5f, 0.5f)
            },
            Vector3f.UNIT_X,
            new Vector2f[] {
                new Vector2f(0.5f, 0), // middle-bottom
                new Vector2f(1, 0),    // front-bottom
                new Vector2f(1, 0.5f),  // front-mid
                new Vector2f(0.5f, 0.5f) // middle-mid
            },
            "side",
            FaceDirection.EAST
        );
        
        // East face - Top part (back upper section)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(1, 0.5f, 1),
                new Vector3f(1, 0.5f, 0.5f),
                new Vector3f(1, 1, 0.5f),
                new Vector3f(1, 1, 1)
            },
            Vector3f.UNIT_X,
            new Vector2f[] {
                new Vector2f(0, 0.5f),    // back-mid
                new Vector2f(0.5f, 0.5f), // middle-mid
                new Vector2f(0.5f, 1),     // middle-top
                new Vector2f(0, 1)       // back-top
            },
            "side",
            FaceDirection.EAST
        );
        
        // East face - Diagonal connector (fills the gap)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(1, 0, 1),
                new Vector3f(1, 0, 0.5f),
                new Vector3f(1, 0.5f, 0.5f),
                new Vector3f(1, 0.5f, 1)
            },
            Vector3f.UNIT_X,
            new Vector2f[] {
                new Vector2f(0, 0),    // back-bottom
                new Vector2f(0.5f, 0), // middle-bottom
                new Vector2f(0.5f, 0.5f), // middle-mid
                new Vector2f(0, 0.5f) // back-mid
            },
            "side",
            FaceDirection.EAST
        );

        // West face - Bottom part (front lower section)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0, 0.5f),
                new Vector3f(0, 0.5f, 0.5f),
                new Vector3f(0, 0.5f, 0)
            },
            Vector3f.UNIT_X.negate(),
            new Vector2f[] {
                new Vector2f(1, 0),      // front-bottom
                new Vector2f(0.5f, 0),   // middle-bottom
                new Vector2f(0.5f, 0.5f), // middle-mid
                new Vector2f(1, 0.5f)   // front-mid
            },
            "side",
            FaceDirection.WEST
        );
        
        // West face - Top part (back upper section)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(0, 0.5f, 0.5f),
                new Vector3f(0, 0.5f, 1),
                new Vector3f(0, 1, 1),
                new Vector3f(0, 1, 0.5f)
            },
            Vector3f.UNIT_X.negate(),
            new Vector2f[] {
                new Vector2f(0.5f, 0.5f), // middle-mid
                new Vector2f(0, 0.5f),    // back-mid
                new Vector2f(0, 1),        // back-top
                new Vector2f(0.5f, 1)    // middle-top
            },
            "side",
            FaceDirection.WEST
        );
        
        // West face - Diagonal connector (fills the gap)
        stairs.addFace(
            new Vector3f[] {
                new Vector3f(0, 0, 0.5f),
                new Vector3f(0, 0, 1),
                new Vector3f(0, 0.5f, 1),
                new Vector3f(0, 0.5f, 0.5f)
            },
            Vector3f.UNIT_X.negate(),
            new Vector2f[] {
                new Vector2f(0.5f, 0),    // middle-bottom
                new Vector2f(0, 0),       // back-bottom
                new Vector2f(0, 0.5f),     // back-mid
                new Vector2f(0.5f, 0.5f) // middle-mid
            },
            "side",
            FaceDirection.WEST
        );

        return stairs;
    }

    /**
     * Fence post (thin vertical post with cross-section)
     */
    public static final BlockGeometry FENCE_POST = createFencePost();

    private static BlockGeometry createFencePost() {
        BlockGeometry fence = new BlockGeometry();
        float thickness = 0.125f; // 2/16 blocks
        float center = 0.5f;
        float min = center - thickness;
        float max = center + thickness;

        // Top face
        fence.addFace(
            new Vector3f[] {
                new Vector3f(min, 1, min),
                new Vector3f(min, 1, max),
                new Vector3f(max, 1, max),
                new Vector3f(max, 1, min)
            },
            Vector3f.UNIT_Y,
            STANDARD_UVS,
            "top",
            FaceDirection.UP
        );

        // Bottom face
        fence.addFace(
            new Vector3f[] {
                new Vector3f(min, 0, min),
                new Vector3f(max, 0, min),
                new Vector3f(max, 0, max),
                new Vector3f(min, 0, max)
            },
            Vector3f.UNIT_Y.negate(),
            STANDARD_UVS,
            "bottom",
            FaceDirection.DOWN
        );

        // North face
        fence.addFace(
            new Vector3f[] {
                new Vector3f(min, 0, max),
                new Vector3f(max, 0, max),
                new Vector3f(max, 1, max),
                new Vector3f(min, 1, max)
            },
            Vector3f.UNIT_Z,
            STANDARD_UVS,
            "side",
            FaceDirection.NONE
        );

        // South face
        fence.addFace(
            new Vector3f[] {
                new Vector3f(max, 0, min),
                new Vector3f(min, 0, min),
                new Vector3f(min, 1, min),
                new Vector3f(max, 1, min)
            },
            Vector3f.UNIT_Z.negate(),
            STANDARD_UVS,
            "side",
            FaceDirection.NONE
        );

        // East face
        fence.addFace(
            new Vector3f[] {
                new Vector3f(max, 0, max),
                new Vector3f(max, 0, min),
                new Vector3f(max, 1, min),
                new Vector3f(max, 1, max)
            },
            Vector3f.UNIT_X,
            STANDARD_UVS,
            "side",
            FaceDirection.NONE
        );

        // West face
        fence.addFace(
            new Vector3f[] {
                new Vector3f(min, 0, min),
                new Vector3f(min, 0, max),
                new Vector3f(min, 1, max),
                new Vector3f(min, 1, min)
            },
            Vector3f.UNIT_X.negate(),
            STANDARD_UVS,
            "side",
            FaceDirection.NONE
        );

        return fence;
    }

    /**
     * Utility: Rotate a block geometry around Y axis
     * @param geometry Original geometry
     * @param rotations Number of 90-degree rotations (0-3)
     * @return New rotated geometry
     */
    public static BlockGeometry rotateY(BlockGeometry geometry, int rotations) {
        BlockGeometry rotated = new BlockGeometry();
        rotations = rotations % 4;

        for (Face face : geometry.getFaces()) {
            Vector3f[] newVertices = new Vector3f[4];
            for (int i = 0; i < 4; i++) {
                newVertices[i] = rotateVertexY(face.vertices[i], rotations);
            }

            Vector3f newNormal = rotateVertexY(face.normal, rotations);
            FaceDirection newDirection = rotateFaceDirection(face.direction, rotations);

            rotated.addFace(newVertices, newNormal, face.uvs, face.textureKey, newDirection);
        }

        return rotated;
    }

    private static Vector3f rotateVertexY(Vector3f v, int rotations) {
        Vector3f result = v.clone();
        for (int i = 0; i < rotations; i++) {
            float newX = result.z;
            float newZ = 1 - result.x;
            result = new Vector3f(newX, result.y, newZ);
        }
        return result;
    }

    private static FaceDirection rotateFaceDirection(FaceDirection dir, int rotations) {
        if (dir == FaceDirection.UP || dir == FaceDirection.DOWN || dir == FaceDirection.NONE) {
            return dir;
        }

        FaceDirection[] directions = {FaceDirection.SOUTH, FaceDirection.EAST, FaceDirection.NORTH, FaceDirection.WEST};
        int index = 0;
        for (int i = 0; i < directions.length; i++) {
            if (directions[i] == dir) {
                index = i;
                break;
            }
        }
        return directions[(index + rotations) % 4];
    }
}
