package com.minecraftclone.entitiy;

import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.input.ActionInput;
import com.minecraftclone.input.KeyMapping;

public class EntityManager {

    private PlayerCharacter playerCharacter;

    public EntityManager(BulletAppState bulletAppState, Node rootNode, Camera cam, InputManager inputManager, ActionInput actionInput) {
        new KeyMapping(inputManager, actionInput.getActionListener());
        playerCharacter = new PlayerCharacter(bulletAppState, actionInput, cam);
        rootNode.attachChild(playerCharacter.getNode());
    }

    public void tick() {
        playerCharacter.tick();
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }
}
