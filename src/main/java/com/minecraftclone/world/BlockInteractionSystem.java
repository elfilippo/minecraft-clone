package com.minecraftclone.world;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.WireBox;
import com.minecraftclone.block.Block;
import com.minecraftclone.player.PlayerCharacter;
import com.minecraftclone.player.input.Action;
import com.minecraftclone.player.input.ActionInput;

public final class BlockInteractionSystem {

    //IS: default max travel distance of ray
    private static final float DEFAULT_REACH = 6.0f;

    //IS: how often ray checks for block
    private static final float RAY_STEP = 0.02f;

    private final World world;
    private final Camera camera;
    private final ActionInput input;
    private final Geometry selectionBox;
    private final SimpleApplication app;

    private Block selectedBlock;

    //INFO: to allow for modifying reach while ingame
    private float reachDistance = DEFAULT_REACH;

    private boolean allowBreaking = true;
    private boolean allowPlacing = true;

    //INFO: to distinguish between holding and tapping place speeds
    private int ticksSinceBreak;
    private int ticksSincePlace;

    //INFO: holding place and break delays in ticks
    private int placeDelay = 8;
    private int breakDelay = 8;

    RaycastResult hit;

    public BlockInteractionSystem(World world, ActionInput input, SimpleApplication app) {
        this.world = world;
        this.camera = app.getCamera();
        this.input = input;
        this.app = app;
        selectionBox = new Geometry("selection", new WireBox(0.501f, 0.501f, 0.501f));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black);
        mat.getAdditionalRenderState().setLineWidth(4f);
        selectionBox.setMaterial(mat);
    }

    /**
     * checks for block breaking and placing
     */
    public void tick() {
        blockSelectionBox();

        ticksSinceBreak += 1;
        ticksSincePlace += 1;

        if (input.isTapped(Action.BREAK_BLOCK) && input.isTapped(Action.PLACE_BLOCK)) tryReplace();
        else if (input.isTapped(Action.BREAK_BLOCK) && allowBreaking) {
            tryBreak();
            return;
        } else if (input.isTapped(Action.PLACE_BLOCK) && allowPlacing) {
            tryPlace();
            return;
        }

        if (input.isHeld(Action.PLACE_BLOCK) && input.isHeld(Action.BREAK_BLOCK)) tryReplace();
        else if (input.isHeld(Action.BREAK_BLOCK) && allowBreaking && ticksSinceBreak > breakDelay) tryBreak();
        else if (input.isHeld(Action.PLACE_BLOCK) && allowPlacing && ticksSincePlace > placeDelay) tryPlace();
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

    /**
     * tries to break clostest block player is looking at
     */
    private void tryBreak() {
        //CASE: block not hit or unloaded
        if (hit == null || !world.isBlockLoaded(hit.x, hit.y, hit.z)) return;

        Block block = world.getBlock(hit.x, hit.y, hit.z);

        //CASE: block not there or not breakable
        if (block == null || !block.isBreakable()) return;

        world.setBlock(hit.x, hit.y, hit.z, null);
        ticksSinceBreak = 0;
    }

    /**
     * tries to place block where player is looking
     */
    private void tryPlace() {
        if (selectedBlock == null) return;

        //CASE: no block hit
        if (hit == null) return;

        //DOES: get place position
        int placeX = hit.x + hit.nx;
        int placeY = hit.y + hit.ny;
        int placeZ = hit.z + hit.nz;

        //CASE: block already present
        if (world.getBlock(placeX, placeY, placeZ) != null) return;

        //CASE: block can't be placed
        //INFO: block-specific placement rules
        if (!selectedBlock.canBePlacedAt(world, placeX, placeY, placeZ)) return;

        //CASE: if block would collide with player
        if (collidesWithPlayer(placeX, placeY, placeZ)) return;

        world.setBlock(placeX, placeY, placeZ, selectedBlock);
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

        //DOES: create coordinates of player bounding box
        float pMinX = pPos.x - pHalfWidth;
        float pMaxX = pPos.x + pHalfWidth;
        float pMinY = pPos.y - pHalfHeight;
        float pMaxY = pPos.y + pHalfHeight;
        float pMinZ = pPos.z - pHalfWidth;
        float pMaxZ = pPos.z + pHalfWidth;

        //DOES: check if any part of block overlaps bounding box
        //CASE: true when overlaps
        boolean overlapX = pMinX < (blockX + 1) && pMaxX > blockX;
        boolean overlapY = pMinY < (blockY + 1) && pMaxY > blockY;
        boolean overlapZ = pMinZ < (blockZ + 1) && pMaxZ > blockZ;

        //CASE: true when all are true
        return overlapX && overlapY && overlapZ;
    }

    /**
     * coordinates of raycast as immutable class
     */
    private static final class RaycastResult {

        final int x, y, z;
        final int nx, ny, nz;

        /**
         * stores coordinates of raycast immutably
         * @param x block x
         * @param y block y
         * @param z block z
         * @param nx normal x
         * @param ny normal y
         * @param nz normal z
         */
        RaycastResult(int x, int y, int z, int nx, int ny, int nz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
        }
    }

    private void blockSelectionBox() {
        hit = raycastBlock();

        //CASE: block not hit or unloaded
        if (hit == null || !world.isBlockLoaded(hit.x, hit.y, hit.z)) {
            selectionBox.removeFromParent();
            return;
        }

        selectionBox.setLocalTranslation(hit.x + 0.5f, hit.y + 0.5f, hit.z + 0.5f);
        if (selectionBox.getParent() == null) {
            app.getRootNode().attachChild(selectionBox);
        }
    }

    private void tryReplace() {
        //CASE: block not hit or unloaded
        if (hit == null || !world.isBlockLoaded(hit.x, hit.y, hit.z)) return;

        Block block = world.getBlock(hit.x, hit.y, hit.z);

        //CASE: block not there or not breakable
        if (block == null || !block.isBreakable()) return;

        if (selectedBlock != block) world.setBlock(hit.x, hit.y, hit.z, selectedBlock);
    }
}
