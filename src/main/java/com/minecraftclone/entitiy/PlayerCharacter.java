package com.minecraftclone.entitiy;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.Node;

public class PlayerCharacter {

    private CharacterControl playerControl;
    private Node playerNode;

    private final float STEP_HEIGHT = 0.5f;
    private final boolean debugEnabled = false;

    public PlayerCharacter(BulletAppState bulletAppState) {
        bulletAppState.setDebugEnabled(debugEnabled);

        var shape = new CapsuleCollisionShape(0.5f, 1.8f);
        var player = new CharacterControl(shape, STEP_HEIGHT);
        player.setJumpSpeed(10f);
        player.setFallSpeed(20f);
        player.setGravity(30f);

        Node playerNode = new Node("Player");
        playerNode.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
        this.playerControl = player;
        this.playerNode = playerNode;
    }

    public Node getNode() {
        return playerNode;
    }

    public CharacterControl getPlayerControl() {
        return playerControl;
    }
}
