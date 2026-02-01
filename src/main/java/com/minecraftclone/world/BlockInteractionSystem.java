package com.minecraftclone.world;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.block.Block;
import com.minecraftclone.entitiy.PlayerCharacter;
import com.minecraftclone.input.ActionInput;

public final class BlockInteractionSystem {

    // =========================
    // CONFIG
    // =========================

    private static final float DEFAULT_REACH = 5.0f;

    // =========================
    // DEPENDENCIES
    // =========================

    private final World world;
    private final Node collisionRoot;
    private final Camera camera;
    private final ActionInput input;

    // =========================
    // STATE
    // =========================

    private Block selectedBlock;
    private float reachDistance = DEFAULT_REACH;

    private boolean allowBreaking = true;
    private boolean allowPlacing = true;

    // =========================
    // CONSTRUCTOR
    // =========================

    public BlockInteractionSystem(World world, Node collisionRoot, Camera camera, ActionInput input) {
        this.world = world;
        this.collisionRoot = collisionRoot;
        this.camera = camera;
        this.input = input;
    }

    // =========================
    // EXTERNAL API
    // =========================

    public void update() {
        boolean breakNow = input.breakBlock();

        if (breakNow && allowBreaking) {
            tryBreak();
        }
        if (input.placeBlock() && allowPlacing) {
            tryPlace();
        }
    }

    public void setSelectedBlock(Block block) {
        this.selectedBlock = block;
    }

    public void setReachDistance(float reach) {
        this.reachDistance = reach;
    }

    public void setAllowBreaking(boolean value) {
        this.allowBreaking = value;
    }

    public void setAllowPlacing(boolean value) {
        this.allowPlacing = value;
    }

    // =========================
    // CORE LOGIC
    // =========================

    private void tryBreak() {
        System.out.println("Attempting to break block...");
        RayHit hit = raycast();
        if (hit == null) {
            System.out.println("Raycast did not hit any block.");
            return;
        }
        System.out.println("Raycast hit a block at: " + hit.x + ", " + hit.y + ", " + hit.z);

        if (!world.isBlockLoaded(hit.x, hit.y, hit.z)) {
            System.out.println("Block is not loaded in the world.");
            return;
        }
        System.out.println("Block is loaded.");

        Block b = world.getBlock(hit.x, hit.y, hit.z);
        if (b == null) {
            System.out.println("No block found at the hit location.");
            return;
        }
        System.out.println("Block found: " + b);

        if (!b.isBreakable()) {
            System.out.println("Block is not breakable.");
            return;
        }
        System.out.println("Block is breakable.");

        world.setBlock(hit.x, hit.y, hit.z, null);
        System.out.println("Block broken successfully.");
    }

    private void tryPlace() {
        System.out.println("Attempting to place block...");
        if (selectedBlock == null) {
            System.out.println("No block selected to place.");
            return;
        }
        System.out.println("Selected block: " + selectedBlock);

        RayHit hit = raycast();
        if (hit == null) {
            System.out.println("Raycast did not hit any block.");
            return;
        }
        System.out.println("Raycast hit a block at: " + hit.x + ", " + hit.y + ", " + hit.z);

        int px = hit.x + hit.nx;
        int py = hit.y + hit.ny;
        int pz = hit.z + hit.nz;
        System.out.println("Calculated placement position: " + px + ", " + py + ", " + pz);

        if (!world.isBlockLoaded(px, py, pz)) {
            System.out.println("Placement location is not loaded.");
            return;
        }
        System.out.println("Placement location is loaded.");

        if (world.getBlock(px, py, pz) != null) {
            System.out.println("A block already exists at the placement location.");
            return;
        }
        System.out.println("Placement location is empty.");

        if (!selectedBlock.canBePlacedAt(world, px, py, pz)) {
            System.out.println("Block cannot be placed at the location.");
            return;
        }
        System.out.println("Block can be placed at the location.");

        if (collidesWithPlayer(px, py, pz)) return;

        world.setBlock(px, py, pz, selectedBlock);
        System.out.println("Block placed successfully.");
    }

    // =========================
    // RAYCASTING
    // =========================

    private RayHit raycast() {
        CollisionResults results = new CollisionResults();

        Ray ray = new Ray(camera.getLocation(), camera.getDirection());
        ray.setLimit(reachDistance);

        collisionRoot.collideWith(ray, results);

        if (results.size() == 0) return null;

        CollisionResult hit = results.getClosestCollision();

        Vector3f p = hit.getContactPoint();
        Vector3f n = hit.getContactNormal();

        final float EPSILON = 0.001f;

        Vector3f biasedPoint = p.subtract(n.mult(EPSILON));

        int x = (int) Math.floor(biasedPoint.x);
        int y = (int) Math.floor(biasedPoint.y);
        int z = (int) Math.floor(biasedPoint.z);

        int nx = normalToAxis(n.x);
        int ny = normalToAxis(n.y);
        int nz = normalToAxis(n.z);

        return new RayHit(x, y, z, nx, ny, nz);
    }

    private int normalToAxis(float v) {
        if (v > 0.5f) return 1;
        if (v < -0.5f) return -1;
        return 0;
    }

    // =========================
    // INTERNAL DATA STRUCT
    // =========================

    private static final class RayHit {

        final int x, y, z;
        final int nx, ny, nz;

        RayHit(int x, int y, int z, int nx, int ny, int nz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
        }
    }

    private boolean collidesWithPlayer(int x, int y, int z) {
        CharacterControl player = world.getPlayerCharacter().getPlayerControl();

        // Block AABB
        Vector3f blockMin = new Vector3f(x, y, z);
        Vector3f blockMax = blockMin.add(1, 1, 1);

        // Player capsule
        Vector3f playerPos = player.getPhysicsLocation(); // bottom center of capsule
        float radius = PlayerCharacter.RADIUS;
        float height = PlayerCharacter.HEIGHT;

        // Convert capsule to AABB
        Vector3f playerMin = playerPos.add(-radius, -0.5f * height, -radius);
        Vector3f playerMax = playerPos.add(radius, 0.5f * height, radius);

        // Check overlap (AABB vs AABB)
        boolean overlapX = blockMin.x < playerMax.x && blockMax.x > playerMin.x;
        boolean overlapY = blockMin.y < playerMax.y && blockMax.y > playerMin.y;
        boolean overlapZ = blockMin.z < playerMax.z && blockMax.z > playerMin.z;

        boolean collision = overlapX && overlapY && overlapZ;

        if (collision) {
            System.out.println("Block at " + x + "," + y + "," + z + " collides with player.");
        }

        return collision;
    }
}
