package com.minecraftclone;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.font.BitmapText;
import com.jme3.system.AppSettings;
import com.minecraftclone.entitiy.PlayerCharacter;
import com.minecraftclone.render.RenderEngine;
import com.minecraftclone.world.*;

public class Main extends SimpleApplication {

    private CharacterControl playerControl;
    private PlayerCharacter playerCharacter;
    private RenderEngine engine;
    private ActionInput actionInput;
    private float timeAccumulator;
    private final float ticksPerSecond = 20f;
    private float tickTime;
    private int totalTicks;
    private BitmapText tpsText;
    private long initialTime;
    private double timeActiveSeconds;

    public static void main(String[] args) {
        var settings = new AppSettings(true);
        settings.setWindowSize(1920, 1080);
        settings.setTitle("minecraft-clone v0.1 by Mats & Filip");

        Main app = new Main();
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initialTime = System.nanoTime();
        tickTime = 1 / ticksPerSecond;

        tpsText = new BitmapText(guiFont);
        guiNode.attachChild(tpsText);
        tpsText.setLocalTranslation(10, settings.getHeight() - 20, 0);

        //INFO: Physics bulletAppState
        var bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //INFO: Camera cam
        flyCam.setMoveSpeed(0);
        cam.setFrustumNear(0.2f);
        cam.setFov(70);

        //INFO: Render engine
        //INFO: renders the world and sets it up for now, setup will be moved later
        engine = new RenderEngine(rootNode, assetManager, bulletAppState);

        //INFO: player is CharacterControl playerControl
        playerCharacter = engine.getPlayerCharacter();
        playerControl = playerCharacter.getPlayerControl();

        //INFO: actionInput
        actionInput = new ActionInput(playerControl);
        new KeyMapping(inputManager, actionInput.getActionListener());
    }

    @Override
    public void simpleUpdate(float tpf) {
        tps();
        timeAccumulator += tpf;

        cam.setLocation(playerCharacter.getPlayerControl().getPhysicsLocation().add(0, 0.2f, 0));

        //INFO: runs tick() until it's caught up
        while (timeAccumulator >= tickTime) {
            tick();
            timeAccumulator -= tickTime;
        }
    }

    private void tick() {
        engine.tick(actionInput, cam);
        totalTicks++;
    }

    private void tps() {
        //INFO: ticks are inaccurate with small timeActive, clamped at 20 for the first 10 seconds
        timeActiveSeconds = (System.nanoTime() - initialTime) / 1000000000.0;
        double tps = (Math.floor((10 * totalTicks) / timeActiveSeconds)) / 10;
        if (timeActiveSeconds < 10) tps = Math.clamp(tps, 0, 20);
        tpsText.setText("TPS: " + tps);
    }
}
