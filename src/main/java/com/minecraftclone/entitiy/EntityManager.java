package com.minecraftclone.entitiy;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.minecraftclone.input.ActionInput;
import com.minecraftclone.input.KeyMapping;

public class EntityManager {

    private PlayerCharacter playerCharacter;

    public EntityManager(SimpleApplication app, BulletAppState bulletAppState, ActionInput actionInput) {
        new KeyMapping(app.getInputManager(), actionInput.getActionListener());
        playerCharacter = new PlayerCharacter(bulletAppState, actionInput, app.getCamera());
        app.getRootNode().attachChild(playerCharacter.getNode());
    }

    public void tick() {
        playerCharacter.tick();
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }
}
