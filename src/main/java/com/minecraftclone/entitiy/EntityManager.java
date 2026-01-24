package com.minecraftclone.entitiy;

import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.world.ActionInput;

public class EntityManager {

    private PlayerCharacter playerCharacter;

    public EntityManager(BulletAppState bulletAppState, Node rootNode) {
        playerCharacter = new PlayerCharacter(bulletAppState);
        rootNode.attachChild(playerCharacter.getNode());
    }

    public void tick(ActionInput input, Camera cam) {
        playerCharacter.tick(input, cam);
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }
}
