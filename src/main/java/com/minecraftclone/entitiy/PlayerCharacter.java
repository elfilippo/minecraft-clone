package com.minecraftclone.entitiy;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.input.ActionInput;

public class PlayerCharacter {

    private final CharacterControl playerControl;
    private final Node playerNode;

    private final float stepHeight = 0.2f;
    private final float speed = 0.15f;
    private final boolean debugEnabled = false;
    private final Vector3f walkDir = new Vector3f();
    private final ActionInput input;
    private final Camera cam;

    public PlayerCharacter(BulletAppState bulletAppState, ActionInput input, Camera cam) {
        this.input = input;
        this.cam = cam;

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

        if (input.isForward()) walkDir.addLocal(forward);
        if (input.isBackward()) walkDir.addLocal(forward.negate());
        if (input.isLeft()) walkDir.addLocal(left);
        if (input.isRight()) walkDir.addLocal(left.negate());

        playerControl.setWalkDirection(walkDir);

        if (input.isJump() && playerControl.onGround()) playerControl.jump();
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
