package com.minecraftclone.world;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.minecraftclone.block.Block;
import com.minecraftclone.player.PlayerCharacter;
import com.minecraftclone.player.input.ActionInput;

public final class BlockInteractionSystem {

    private static final float DEFAULT_REACH = 6.0f;
    private static final float STEP = 0.05f; // Ray step size

    private final World world;
    private final Camera camera;
    private final ActionInput input;

    private Block selectedBlock;
    private float reachDistance = DEFAULT_REACH;
    private boolean allowBreaking = true;
    private boolean allowPlacing = true;
    private int ticksSinceBreak;
    private int ticksSincePlace;
    private int placeDelay = 8;
    private int breakDelay = 4;

    public BlockInteractionSystem(World world, Camera camera, ActionInput input) {
        this.world = world;
        this.camera = camera;
        this.input = input;
    }

    public void tick() {
        ticksSinceBreak += 1;
        ticksSincePlace += 1;

        if (input.breakBlock() && allowBreaking) {
            tryBreak();
            return;
        }
        if (input.placeBlock() && allowPlacing) {
            tryPlace();
            return;
        }

        if (input.breakBlockHeld() && allowBreaking && ticksSinceBreak > breakDelay) tryBreak();
        if (input.placeBlockHeld() && allowPlacing && ticksSincePlace > placeDelay) tryPlace();
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
        ticksSinceBreak = 0;
    }

    private void tryPlace() {
        if (selectedBlock == null) return;

        RaycastResult hit = raycastBlock();
        if (hit == null) return; // Must hit a block to place against

        int px = hit.x + hit.nx;
        int py = hit.y + hit.ny;
        int pz = hit.z + hit.nz;

        // Space must be empty
        if (world.getBlock(px, py, pz) != null) return;

        // Block-specific placement rules
        if (!selectedBlock.canBePlacedAt(world, px, py, pz)) return;

        // Player collision check
        if (collidesWithPlayer(px, py, pz)) return;

        world.setBlock(px, py, pz, selectedBlock);
        ticksSincePlace = 0;
    }

    private RaycastResult raycastBlock() {
        Vector3f origin = camera.getLocation();
        Vector3f facingDirection = camera.getDirection().normalize();

        //DOES: cast ray that goes along viewDirection until reachDistance is reached
        for (float rayProgress = 0; rayProgress <= reachDistance; rayProgress += STEP) {
            Vector3f rayPos = origin.add(facingDirection.mult(rayProgress));

            //DOES: calculate block at ray position
            int blockX = (int) Math.floor(rayPos.x);
            int blockY = (int) Math.floor(rayPos.y);
            int blockZ = (int) Math.floor(rayPos.z);

            //DOES: check if block is loaded
            if (world.isBlockLoaded(blockX, blockY, blockZ)) {
                Block block = world.getBlock(blockX, blockY, blockZ);
                if (block != null) {
                    //NOTE: local coordinates inside the block
                    float inBlockX = rayPos.x - blockX;
                    float inBlockY = rayPos.y - blockY;
                    float inBlockZ = rayPos.z - blockZ;

                    //NOTE: normal vector (which side was hit)
                    //NOTE: ex. (0,1,0) for top
                    int normalX = 0,
                        normalY = 0,
                        normalZ = 0;

                    //DOES: calculate closest distance from hit point to cube face
                    float minDistance = Math.min(
                        Math.min(inBlockX, 1 - inBlockX),
                        Math.min(Math.min(inBlockY, 1 - inBlockY), Math.min(inBlockZ, 1 - inBlockZ))
                    );

                    //DOES: use minDistance to set normal vector (decide which face was hit)
                    if (minDistance == inBlockX) normalX = -1;
                    else if (minDistance == 1 - inBlockX) normalX = 1;
                    else if (minDistance == inBlockY) normalY = -1;
                    else if (minDistance == 1 - inBlockY) normalY = 1;
                    else if (minDistance == inBlockZ) normalZ = -1;
                    else if (minDistance == 1 - inBlockZ) normalZ = 1;

                    //DOES: return RaycastResult with block position and face hit
                    return new RaycastResult(blockX, blockY, blockZ, normalX, normalY, normalZ);
                }
            }
        }

        // No block hit
        return null;
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
