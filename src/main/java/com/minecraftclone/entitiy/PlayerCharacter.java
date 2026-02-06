package com.minecraftclone.entitiy;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.minecraftclone.gui.PlayerGUI;
import com.minecraftclone.input.ActionInput;
import java.io.IOException;

public class PlayerCharacter {

    private final CharacterControl playerControl;
    private final Node playerNode;

    private final float stepHeight = 0.2f;
    private final float speed = 0.15f;
    private final boolean debugEnabled = false;
    private final Vector3f walkDir = new Vector3f();
    private final ActionInput input;
    private final Camera cam;

    public PlayerCharacter(
        BulletAppState bulletAppState,
        ActionInput input,
        Camera cam,
        AppSettings settings,
        Node guiNode,
        AssetManager assetManager
    ) {
        this.input = input;
        this.cam = cam;

        try {
            PlayerGUI gui = new PlayerGUI(settings, guiNode, assetManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bulletAppState.setDebugEnabled(debugEnabled);

        var shape = new CapsuleCollisionShape(0.5f, 1.8f);
        var player = new CharacterControl(shape, stepHeight);
        player.setJumpSpeed(10f);
        player.setFallSpeed(20f);
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

        if (input.keyDown('w')) walkDir.addLocal(forward);
        if (input.keyDown('a')) walkDir.addLocal(left);
        if (input.keyDown('s')) walkDir.addLocal(forward.negate());
        if (input.keyDown('d')) walkDir.addLocal(left.negate());

        playerControl.setWalkDirection(walkDir);

        if (input.keyDown(' ') && playerControl.onGround()) playerControl.jump();
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
