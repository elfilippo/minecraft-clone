package com.minecraftclone.player;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.Main;
import com.minecraftclone.gui.PlayerGUI;
import com.minecraftclone.gui.menu.Menus;
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
    private Vector3f previousPosition, currentPosition;
    private final Camera cam;

    private int life, hunger;
    private int hotbarSlot = 1;

    private PlayerGUI gui;
    private Inventory inventory;
    private InventoryController inventoryController;

    public PlayerCharacter(BulletAppState bulletAppState, Main main) {
        cam = main.getCamera();

        //DOES: set debug mode
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

        previousPosition = player.getPhysicsLocation();
        currentPosition = player.getPhysicsLocation();

        this.playerControl = player;
        this.playerNode = playerNode;

        inventory = new Inventory(35);
        try {
            gui = new PlayerGUI(main);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inventoryController = new InventoryController(inventory, gui);
    }

    public void tick(PlayerCommand cmd) {
        //DOES: set previous and current position for camera interpolation
        previousPosition.set(currentPosition);
        currentPosition.set(playerControl.getPhysicsLocation());

        //DOES: walk movement
        //NOTE: some kind of interpolation to be added for smoother movement
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

        //DOES: jumping
        playerControl.setWalkDirection(walkDir);

        if (cmd.jump && playerControl.onGround()) {
            playerControl.jump();
        }

        //DOES: directly select hotbar slot
        if (cmd.selectHotbar != 0) {
            hotbarSlot = cmd.selectHotbar;
        }

        //DOES: mouse wheel stuff
        hotbarSlot += cmd.hotbarDelta;
        hotbarSlot = Math.max(1, Math.min(9, hotbarSlot));
        gui.setHotbarSelectedSlot(hotbarSlot);

        if (cmd.toggleInventory) {
            if (gui.isMenuVisible()) {
                gui.setMenuVisibility(Menus.NONE);
            } else {
                gui.setMenuVisibility(Menus.INVENTORY);
            }
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

    public void setLife(int life) {
        this.life = life;
        gui.setLife(life);
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
        gui.setHunger(hunger);
    }

    public PlayerGUI getGui() {
        return gui;
    }

    /**
     * interpolates between previous and current position
     * @param alpha how far into the tick we are
     * @return
     */
    public Vector3f getInterpolatedPosition(float alpha) {
        //DOES: find position at distance fraction alpha between previous and current pos
        //NOTE: ex. halfway between them at alpha 0.5
        return new Vector3f().interpolateLocal(previousPosition, currentPosition, alpha);
    }
}
