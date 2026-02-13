package com.minecraftclone.player;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.gui.PlayerGUI;
import com.minecraftclone.player.input.Action;
import com.minecraftclone.player.input.ActionInput;
import java.io.IOException;

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
    private PlayerGUI gui;
    private int life = 13;
    private int hunger = 13;

    public PlayerCharacter(BulletAppState bulletAppState, ActionInput input, SimpleApplication app) {
        this.input = input;
        cam = app.getCamera();

        //TODO: move gui to RenderEngine
        try {
            gui = new PlayerGUI(app);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gui.setLife(life);
        gui.setHunger(hunger);
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
        Vector3f forward = cam.getDirection().clone();
        forward.setY(0).normalizeLocal().multLocal(speed);
        Vector3f left = cam.getLeft().clone();
        left.setY(0).normalizeLocal().multLocal(speed);

        walkDir.set(0, 0, 0);

        if (input.isHeld(Action.FORWARD)) walkDir.addLocal(forward);
        if (input.isHeld(Action.LEFT)) walkDir.addLocal(left);
        if (input.isHeld(Action.BACKWARD)) walkDir.addLocal(forward.negate());
        if (input.isHeld(Action.RIGHT)) walkDir.addLocal(left.negate());

        playerControl.setWalkDirection(walkDir);
        if (input.isHeld(Action.JUMP) && playerControl.onGround()) playerControl.jump();

        if (input.isTapped(Action.HOTBAR_1)) gui.changeHotbarSlot(1);
        if (input.isTapped(Action.HOTBAR_2)) gui.changeHotbarSlot(2);
        if (input.isTapped(Action.HOTBAR_3)) gui.changeHotbarSlot(3);
        if (input.isTapped(Action.HOTBAR_4)) gui.changeHotbarSlot(4);
        if (input.isTapped(Action.HOTBAR_5)) gui.changeHotbarSlot(5);
        if (input.isTapped(Action.HOTBAR_6)) gui.changeHotbarSlot(6);
        if (input.isTapped(Action.HOTBAR_7)) gui.changeHotbarSlot(7);
        if (input.isTapped(Action.HOTBAR_8)) gui.changeHotbarSlot(8);
        if (input.isTapped(Action.HOTBAR_9)) gui.changeHotbarSlot(9);

        if (input.isTapped(Action.TOGGLE_INVENTORY)) gui.toggleInventory();
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
}
