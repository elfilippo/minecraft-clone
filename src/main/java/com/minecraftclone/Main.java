package com.minecraftclone;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.controls.ActionListener;
import com.minecraftclone.entitiy.PlayerCharacter;
import com.minecraftclone.render.RenderEngine;
import com.minecraftclone.world.*;

/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class Main extends SimpleApplication {

    private CharacterControl playerControl;
    private PlayerCharacter player;
    private RenderEngine engine;

    public static void main(String[] args) {
        Main app = new Main();
        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        new KeyMapping(inputManager, actionListener);

        var bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        flyCam.setMoveSpeed(0);
        cam.setFrustumNear(0.8f);

        engine = new RenderEngine(rootNode, assetManager, bulletAppState);

        //gets player Spatial straight from object manager (root node)
        //Spatial player = rootNode.getChild("Player");
        //playerControl = player.getControl(CharacterControl.class);
        player = engine.getPlayerCharacter();
        playerControl = player.getPlayerControl();
    }

    @Override
    public void simpleUpdate(float tpf) {
        movement();
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            new ActionInput(name, keyPressed, tpf, playerControl);
        }
    };

    private void movement() {}
}
