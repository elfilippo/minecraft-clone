package com.minecraftclone.player;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.player.input.Action;
import com.minecraftclone.player.input.ActionInput;

public class PlayerCharacter {

    public static final float STEP_HEIGHT = 0.1f;
    public static final float WIDTH = 0.7f;
    public static final float HEIGHT = 1.8f;
    public static final float EYE_OFFSET = HEIGHT * 0.35f;
    private final CharacterControl playerControl;
    private final Node playerNode;

    private final float speed = 0.15f;
    private final boolean debugEnabled = false;
    private final Vector3f walkDir = new Vector3f();
    private final ActionInput input;
    private final Camera cam;
    private int life = 13;
    private int hunger = 13;
    private int hotbarSlot = 1;
    private boolean inventoryVisible = false;

    public PlayerCharacter(BulletAppState bulletAppState, ActionInput input, SimpleApplication app) {
        this.input = input;
        cam = app.getCamera();

        bulletAppState.setDebugEnabled(debugEnabled);

        var shape = new BoxCollisionShape(new Vector3f(WIDTH / 2f, HEIGHT / 2f, WIDTH / 2f));
        var player = new CharacterControl(shape, STEP_HEIGHT);
        player.setJumpSpeed(10f);
        player.setFallSpeed(40f);
        player.setGravity(30f);

        Node playerNode = new Node("Player");
        playerNode.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);

        player.setPhysicsLocation(new Vector3f(5, 20, 2));
        this.playerControl = player;
        this.playerNode = playerNode;
    }

    public void tick() {
        //DOES: walk movement
        //NOTE: some kind of interpolation to be added for smoother movement
        Vector3f forward = cam.getDirection().clone();
        forward.setY(0).normalizeLocal().multLocal(speed);
        Vector3f left = cam.getLeft().clone();
        left.setY(0).normalizeLocal().multLocal(speed);

        walkDir.set(0, 0, 0);

        if (input.isHeld(Action.FORWARD)) walkDir.addLocal(forward);
        if (input.isHeld(Action.LEFT)) walkDir.addLocal(left);
        if (input.isHeld(Action.BACKWARD)) walkDir.addLocal(forward.negate());
        if (input.isHeld(Action.RIGHT)) walkDir.addLocal(left.negate());

        //DOES: jumping
        playerControl.setWalkDirection(walkDir);
        if (input.isHeld(Action.JUMP) && playerControl.onGround()) playerControl.jump();

        Action[] hotbar = {
            Action.HOTBAR_1,
            Action.HOTBAR_2,
            Action.HOTBAR_3,
            Action.HOTBAR_4,
            Action.HOTBAR_5,
            Action.HOTBAR_6,
            Action.HOTBAR_7,
            Action.HOTBAR_8,
            Action.HOTBAR_9,
        };

        //DOES: iterate through hotbar slots and switch to them if key tapped
        for (int i = 0; i < hotbar.length; i++) {
            if (input.isTapped(hotbar[i])) hotbarSlot = i + 1;
        }

        if (input.isTapped(Action.TOGGLE_INVENTORY)) inventoryVisible = !inventoryVisible;
    }

    public Node getNode() {
        return playerNode;
    }

    public CharacterControl getPlayerControl() {
        return playerControl;
    }

    public Vector3f getPosition() {
        return playerControl.getPhysicsLocation();
    }

    public int getLife() {
        return life;
    }

    public int getHunger() {
        return hunger;
    }

    public int getHotbarSlot() {
        return hotbarSlot;
    }

    public boolean getinventoryVisible() {
        return inventoryVisible;
    }
}
