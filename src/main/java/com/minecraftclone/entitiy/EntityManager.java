package com.minecraftclone.entitiy;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.minecraftclone.input.ActionInput;
import com.minecraftclone.input.AnalogInput;
import com.minecraftclone.input.KeyMapping;

public class EntityManager {

    private PlayerCharacter playerCharacter;

    public EntityManager(
        BulletAppState bulletAppState,
        Node rootNode,
        Camera cam,
        InputManager inputManager,
        AppSettings settings,
        Node guiNode,
        AssetManager assetManager
    ) {
        ActionInput actionInput = new ActionInput();
        AnalogInput analogInput = new AnalogInput();
        new KeyMapping(inputManager, actionInput, analogInput);
        playerCharacter = new PlayerCharacter(bulletAppState, actionInput, cam, settings, guiNode, assetManager);
        rootNode.attachChild(playerCharacter.getNode());
    }

    public void tick() {
        playerCharacter.tick();
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }
}
