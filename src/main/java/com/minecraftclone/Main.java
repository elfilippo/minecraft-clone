package com.minecraftclone;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.system.AppSettings;
import com.minecraftclone.entitiy.PlayerCharacter;
import com.minecraftclone.render.RenderEngine;
import com.minecraftclone.world.*;

public class Main extends SimpleApplication {

    private float timeAccumulator;
    private final float ticksPerSecond = 20f;
    private float tickTime;
    private int totalTicks;
    private BitmapText tpsText;
    private long initialTime;
    private double timeActiveSeconds;
    private PlayerCharacter playerCharacter;

    private RenderEngine engine;
    private World world;

    public static void main(String[] args) {
        var settings = new AppSettings(true);
        settings.setWindowSize(1920, 1080);
        settings.setSamples(4);
        settings.setTitle("minecraft-clone v0.2.0-alpha    © Mats O. & Filip M.");

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

        //NOTE: Physics bulletAppState
        var bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //NOTE: Camera cam
        flyCam.setMoveSpeed(0);
        cam.setFrustumNear(0.2f);
        cam.setFov(70);

        //INFO: world owns all data
        world = new World(rootNode, assetManager, bulletAppState, cam, inputManager);

        playerCharacter = world.getPlayerCharacter();

        //NOTE: Render engine
        //INFO: will render the world eventually, does nothing rn
        //new RenderEngine(rootNode, assetManager, bulletAppState);
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
        totalTicks++;
        playerCharacter.tick();
    }

    private void tps() {
        //INFO: ticks are inaccurate with small timeActive, clamped at 20 for the first 10 seconds
        timeActiveSeconds = (System.nanoTime() - initialTime) / 1000000000.0;
        double tps = (Math.floor((10 * totalTicks) / timeActiveSeconds)) / 10;
        if (timeActiveSeconds < 10) tps = Math.clamp(tps, 0, 20);
        tpsText.setText("TPS: " + tps);
    }
}
