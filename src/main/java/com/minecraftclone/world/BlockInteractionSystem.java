package com.minecraftclone.world;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.minecraftclone.block.Block;
import com.minecraftclone.entitiy.PlayerCharacter;
import com.minecraftclone.input.ActionInput;

public final class BlockInteractionSystem {

    private static final float DEFAULT_REACH = 5.0f;
    private static final float STEP = 0.05f; // Ray step size

    private final World world;
    private final Camera camera;
    private final ActionInput input;

    private Block selectedBlock;
    private float reachDistance = DEFAULT_REACH;
    private boolean allowBreaking = true;
    private boolean allowPlacing = true;

    public BlockInteractionSystem(World world, Camera camera, ActionInput input) {
        this.world = world;
        this.camera = camera;
        this.input = input;
    }

    public void update() {
        if (input.breakBlock() && allowBreaking) tryBreak();
        if (input.placeBlock() && allowPlacing) tryPlace();
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
    // BLOCK INTERACTION
    // =========================

    private void tryBreak() {
        RaycastResult hit = raycastBlock();
        if (hit == null) return;
        if (!world.isBlockLoaded(hit.x, hit.y, hit.z)) return;
        Block b = world.getBlock(hit.x, hit.y, hit.z);
        if (b == null || !b.isBreakable()) return;

        world.setBlock(hit.x, hit.y, hit.z, null);
    }

    private void tryPlace() {
        if (selectedBlock == null) return;

        RaycastResult hit = raycastBlock();
        if (hit == null) {
            // Place at max reach in empty space
            Vector3f pos = camera.getLocation().add(camera.getDirection().normalize().mult(reachDistance));
            int px = (int) Math.floor(pos.x);
            int py = (int) Math.floor(pos.y);
            int pz = (int) Math.floor(pos.z);

            if (world.getBlock(px, py, pz) != null) return;
            if (!selectedBlock.canBePlacedAt(world, px, py, pz)) return;
            if (collidesWithPlayer(px, py, pz)) return;

            world.setBlock(px, py, pz, selectedBlock);
            return;
        }

        int px = hit.x + hit.nx;
        int py = hit.y + hit.ny;
        int pz = hit.z + hit.nz;

        if (world.getBlock(px, py, pz) != null) return;
        if (!selectedBlock.canBePlacedAt(world, px, py, pz)) return;
        if (collidesWithPlayer(px, py, pz)) return;

        world.setBlock(px, py, pz, selectedBlock);
    }

    // =========================
    // RAYCAST USING AABB
    // =========================

    private RaycastResult raycastBlock() {
        Vector3f origin = camera.getLocation();
        Vector3f dir = camera.getDirection().normalize();

        RaycastResult lastEmpty = null;

        for (float t = 0; t <= reachDistance; t += STEP) {
            Vector3f pos = origin.add(dir.mult(t));

            int bx = (int) Math.floor(pos.x);
            int by = (int) Math.floor(pos.y);
            int bz = (int) Math.floor(pos.z);

            if (world.isBlockLoaded(bx, by, bz)) {
                Block b = world.getBlock(bx, by, bz);
                if (b != null) {
                    float fx = pos.x - bx;
                    float fy = pos.y - by;
                    float fz = pos.z - bz;

                    int nx = 0,
                        ny = 0,
                        nz = 0;
                    float min = Math.min(Math.min(fx, 1 - fx), Math.min(Math.min(fy, 1 - fy), Math.min(fz, 1 - fz)));
                    if (min == fx) nx = -1;
                    else if (min == 1 - fx) nx = 1;
                    else if (min == fy) ny = -1;
                    else if (min == 1 - fy) ny = 1;
                    else if (min == fz) nz = -1;
                    else if (min == 1 - fz) nz = 1;

                    return new RaycastResult(bx, by, bz, nx, ny, nz);
                }
            }

            lastEmpty = new RaycastResult(bx, by, bz, 0, 0, 0);
        }

        return lastEmpty;
    }

    private boolean collidesWithPlayer(int x, int y, int z) {
        CharacterControl player = world.getPlayerCharacter().getPlayerControl();

        Vector3f blockMin = new Vector3f(x, y, z);
        Vector3f blockMax = blockMin.add(1, 1, 1);

        Vector3f playerPos = player.getPhysicsLocation();
        float radius = PlayerCharacter.RADIUS;
        float height = PlayerCharacter.HEIGHT;
        Vector3f playerMin = playerPos.add(-radius, -0.5f * height, -radius);
        Vector3f playerMax = playerPos.add(radius, 0.5f * height, radius);

        boolean overlapX = blockMin.x < playerMax.x && blockMax.x > playerMin.x;
        boolean overlapY = blockMin.y < playerMax.y && blockMax.y > playerMin.y;
        boolean overlapZ = blockMin.z < playerMax.z && blockMax.z > playerMin.z;

        return overlapX && overlapY && overlapZ;
    }

    private static final class RaycastResult {

        final int x, y, z;
        final int nx, ny, nz;

        RaycastResult(int x, int y, int z, int nx, int ny, int nz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
        }
    }
}
