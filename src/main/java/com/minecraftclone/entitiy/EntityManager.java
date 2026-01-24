package com.minecraftclone.entitiy;

import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class EntityManager {

    private PlayerCharacter playerCharacter;

    public EntityManager(BulletAppState bulletAppState, Node rootNode) {
        playerCharacter = new PlayerCharacter(bulletAppState);
        rootNode.attachChild(playerCharacter.getNode());
        var playerControl = playerCharacter.getPlayerControl();

        playerControl.setPhysicsLocation(new Vector3f(0f, 10f, 0f));
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }
}
