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
    private boolean console = false;
    private char consoleInput;

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

        if (input.isTapped(Action.HOTBAR_1)) hotbarSlot = 1;
        if (input.isTapped(Action.HOTBAR_2)) hotbarSlot = 2;
        if (input.isTapped(Action.HOTBAR_3)) hotbarSlot = 3;
        if (input.isTapped(Action.HOTBAR_4)) hotbarSlot = 4;
        if (input.isTapped(Action.HOTBAR_5)) hotbarSlot = 5;
        if (input.isTapped(Action.HOTBAR_6)) hotbarSlot = 6;
        if (input.isTapped(Action.HOTBAR_7)) hotbarSlot = 7;
        if (input.isTapped(Action.HOTBAR_8)) hotbarSlot = 8;
        if (input.isTapped(Action.HOTBAR_9)) hotbarSlot = 9;

        if (input.isTapped(Action.TOGGLE_INVENTORY)) inventoryVisible = !inventoryVisible;

        if (input.isTapped(Action.T)) console = true;

        if (console) {
            if (input.isTapped(Action.ONE)) consoleInput = '1';
            if (input.isTapped(Action.TWO)) consoleInput = '2';
            if (input.isTapped(Action.THREE)) consoleInput = '3';
            if (input.isTapped(Action.FOUR)) consoleInput = '4';
            if (input.isTapped(Action.FIVE)) consoleInput = '5';
            if (input.isTapped(Action.SIX)) consoleInput = '6';
            if (input.isTapped(Action.SEVEN)) consoleInput = '7';
            if (input.isTapped(Action.EIGHT)) consoleInput = '8';
            if (input.isTapped(Action.NINE)) consoleInput = '9';
            if (input.isTapped(Action.ZERO)) consoleInput = '0';

            if (input.isTapped(Action.A)) consoleInput = 'a';
            if (input.isTapped(Action.B)) consoleInput = 'b';
            if (input.isTapped(Action.C)) consoleInput = 'c';
            if (input.isTapped(Action.D)) consoleInput = 'd';
            if (input.isTapped(Action.E)) consoleInput = 'e';
            if (input.isTapped(Action.F)) consoleInput = 'f';
            if (input.isTapped(Action.G)) consoleInput = 'g';
            if (input.isTapped(Action.H)) consoleInput = 'h';
            if (input.isTapped(Action.I)) consoleInput = 'i';
            if (input.isTapped(Action.J)) consoleInput = 'j';
            if (input.isTapped(Action.K)) consoleInput = 'k';
            if (input.isTapped(Action.M)) consoleInput = 'm';
            if (input.isTapped(Action.N)) consoleInput = 'n';
            if (input.isTapped(Action.L)) consoleInput = 'l';
            if (input.isTapped(Action.O)) consoleInput = 'o';
            if (input.isTapped(Action.P)) consoleInput = 'p';
            if (input.isTapped(Action.Q)) consoleInput = 'q';
            if (input.isTapped(Action.R)) consoleInput = 'r';
            if (input.isTapped(Action.S)) consoleInput = 's';
            if (input.isTapped(Action.T)) consoleInput = 't';
            if (input.isTapped(Action.U)) consoleInput = 'u';
            if (input.isTapped(Action.V)) consoleInput = 'v';
            if (input.isTapped(Action.W)) consoleInput = 'w';
            if (input.isTapped(Action.X)) consoleInput = 'x';
            if (input.isTapped(Action.Y)) consoleInput = 'y';
            if (input.isTapped(Action.Z)) consoleInput = 'z';
        }
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

    public char getConsoleInput() {
        return consoleInput;
    }
}
