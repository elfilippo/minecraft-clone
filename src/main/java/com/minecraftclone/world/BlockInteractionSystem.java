package com.minecraftclone.world;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.minecraftclone.block.Block;
import com.minecraftclone.player.PlayerCharacter;
import com.minecraftclone.player.input.Action;
import com.minecraftclone.player.input.ActionInput;

public final class BlockInteractionSystem {

    private static final float DEFAULT_REACH = 6.0f;
    private static final float RAY_STEP = 0.02f; // Ray step size

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
    private int breakDelay = 8;

    public BlockInteractionSystem(World world, Camera camera, ActionInput input) {
        this.world = world;
        this.camera = camera;
        this.input = input;
    }

    public void tick() {
        ticksSinceBreak += 1;
        ticksSincePlace += 1;

        if (input.isTapped(Action.BREAK_BLOCK) && allowBreaking) {
            tryBreak();
            return;
        }
        if (input.isTapped(Action.PLACE_BLOCK) && allowPlacing) {
            tryPlace();
            return;
        }

        if (input.isHeld(Action.BREAK_BLOCK) && allowBreaking && ticksSinceBreak > breakDelay) tryBreak();
        if (input.isHeld(Action.PLACE_BLOCK) && allowPlacing && ticksSincePlace > placeDelay) tryPlace();
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

    /**
     * gets block player is looking at if within range
     * @return coordinates and normals of first hit block as RaycastResult
     */
    private RaycastResult raycastBlock() {
        Vector3f origin = camera.getLocation();
        Vector3f facingDirection = camera.getDirection().normalize();

        int lastBlockX = (int) Math.floor(origin.x);
        int lastBlockY = (int) Math.floor(origin.y);
        int lastBlockZ = (int) Math.floor(origin.z);

        //DOES: cast ray that goes along viewDirection until reachDistance is reached
        for (float rayProgress = 0; rayProgress <= reachDistance; rayProgress += RAY_STEP) {
            Vector3f rayPos = origin.add(facingDirection.mult(rayProgress));

            //DOES: calculate block at ray position
            int blockX = (int) Math.floor(rayPos.x);
            int blockY = (int) Math.floor(rayPos.y);
            int blockZ = (int) Math.floor(rayPos.z);

            //DOES: skip checks if in same block
            if (blockX == lastBlockX && blockY == lastBlockY && blockZ == lastBlockZ) continue;

            //DOES: check if block is loaded
            if (world.isBlockLoaded(blockX, blockY, blockZ)) {
                Block block = world.getBlock(blockX, blockY, blockZ);
                if (block != null) {
                    //NOTE: normal vector (which side was hit)
                    //NOTE: ex. (0,1,0) for top
                    int normalX = 0,
                        normalY = 0,
                        normalZ = 0;

                    //DOES: set normals based on what block ray was last in
                    if (lastBlockX == blockX + 1) normalX = 1;
                    else if (lastBlockX == blockX - 1) normalX = -1;
                    else if (lastBlockY == blockY + 1) normalY = 1;
                    else if (lastBlockY == blockY - 1) normalY = -1;
                    else if (lastBlockZ == blockZ + 1) normalZ = 1;
                    else if (lastBlockZ == blockZ - 1) normalZ = -1;

                    //DOES: return RaycastResult with block position and face hit
                    return new RaycastResult(blockX, blockY, blockZ, normalX, normalY, normalZ);
                } else {
                    lastBlockX = blockX;
                    lastBlockY = blockY;
                    lastBlockZ = blockZ;
                }
            } else {
                lastBlockX = blockX;
                lastBlockY = blockY;
                lastBlockZ = blockZ;
            }
        }
        //CASE: no block hit
        return null;
    }

    /**
     * checks if the given block would collide with the player
     * @param blockX
     * @param blockY
     * @param blockZ
     * @return
     */
    private boolean collidesWithPlayer(int blockX, int blockY, int blockZ) {
        Vector3f pPos = world.getPlayerCharacter().getPlayerControl().getPhysicsLocation();

        float pHalfWidth = PlayerCharacter.WIDTH / 2f;
        float pHalfHeight = PlayerCharacter.HEIGHT / 2f;

        float pMinX = pPos.x - pHalfWidth;
        float pMaxX = pPos.x + pHalfWidth;
        float pMinY = pPos.y - pHalfHeight;
        float pMaxY = pPos.y + pHalfHeight;
        float pMinZ = pPos.z - pHalfWidth;
        float pMaxZ = pPos.z + pHalfWidth;

        boolean overlapX = pMinX < (blockX + 1) && pMaxX > blockX;
        boolean overlapY = pMinY < (blockY + 1) && pMaxY > blockY;
        boolean overlapZ = pMinZ < (blockZ + 1) && pMaxZ > blockZ;

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
