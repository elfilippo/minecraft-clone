package com.minecraftclone.entitiy;

import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.Helper;
import com.minecraftclone.world.ActionInput;

public class EntityManager {

    private PlayerCharacter playerCharacter;
    private Node entities;

    public EntityManager(BulletAppState bulletAppState, Node rootNode, Helper helper) {
        entities = new Node();
        rootNode.attachChild(entities);
        playerCharacter = new PlayerCharacter(bulletAppState, helper);
        entities.attachChild(playerCharacter.getNode());
    }

    public void tick(ActionInput input, Camera cam) {
        playerCharacter.tick(input, cam);
    }

    public PlayerCharacter createPlayerCharacter() {
        return playerCharacter;
    }
}
