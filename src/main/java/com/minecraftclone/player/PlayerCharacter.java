package com.minecraftclone.player;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.gui.menu.Menus;

public class PlayerCharacter {

    public static final float STEP_HEIGHT = 0.1f;
    public static final float WIDTH = 0.7f;
    public static final float HEIGHT = 1.8f;
    public static final float EYE_OFFSET = HEIGHT * 0.35f;
    private final CharacterControl playerControl; //Info: Can replace with BetterCharacterControl
    private final Node playerNode;

    private final float speed = 0.15f;
    private final boolean debugEnabled = false;
    private final Vector3f walkDir = new Vector3f();
    private final Camera cam;
    private int life = 13;
    private int hunger = 13;
    private int hotbarSlot = 1;
    private Menus menu;

    public PlayerCharacter(BulletAppState bulletAppState, SimpleApplication app) {
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

    public void tick(PlayerCommand cmd) {
        Vector3f forward = cam.getDirection().clone();
        forward.setY(0).normalizeLocal();

        Vector3f left = cam.getLeft().clone();
        left.setY(0).normalizeLocal();

        walkDir.set(0, 0, 0);

        walkDir.addLocal(forward.mult(cmd.forward));
        walkDir.addLocal(left.mult(cmd.strafe));

        if (walkDir.lengthSquared() > 0) {
            walkDir.normalizeLocal().multLocal(speed);
        }

        playerControl.setWalkDirection(walkDir);

        if (cmd.jump && playerControl.onGround()) {
            playerControl.jump();
        }

        // Hotbar direct selection
        if (cmd.selectHotbar != 0) {
            hotbarSlot = cmd.selectHotbar;
        }

        // Mouse wheel
        hotbarSlot += cmd.hotbarDelta;
        hotbarSlot = Math.max(1, Math.min(9, hotbarSlot));

        /*if (cmd.toggleInventory) {
            menu.toggleInventory();
        }*/ //FIXME:
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
}
