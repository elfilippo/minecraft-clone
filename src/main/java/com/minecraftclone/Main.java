package com.minecraftclone;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.system.AppSettings;
import com.minecraftclone.block.Blocks;
import com.minecraftclone.player.PlayerCharacter;
import com.minecraftclone.player.input.ActionInput;
import com.minecraftclone.player.input.AnalogInput;
import com.minecraftclone.player.input.KeyMapping;
import com.minecraftclone.world.BlockInteractionSystem;
import com.minecraftclone.world.World;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends SimpleApplication {

    private float timeAccumulator;
    private final float ticksPerSecond = 40f;
    private float tickTime;
    private int totalTicks;
    private BitmapText tpsText;
    private long initialTime;
    private double timeActiveSeconds;
    private PlayerCharacter playerCharacter;

    private World world;

    // =========================
    // NEW
    // =========================

    private ActionInput actionInput;
    private AnalogInput analogInput;
    private BlockInteractionSystem blockInteraction;

    public static void main(String[] args) {
        disableWarnings();
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
        tickTime = 1f / ticksPerSecond;

        tpsText = new BitmapText(guiFont);
        guiNode.attachChild(tpsText);
        tpsText.setLocalTranslation(10, settings.getHeight() - 20, 0);

        //NOTE: Physics bulletAppState
        var bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //NOTE: Camera cam
        flyCam.setEnabled(true);
        cam.setFrustumNear(0.2f);
        cam.setFov(70);
        getRenderer().setDefaultAnisotropicFilter(4);

        //INFO: world owns all data
        actionInput = new ActionInput();
        analogInput = new AnalogInput();
        new KeyMapping(inputManager, actionInput, flyCam);
        world = new World(this, actionInput, analogInput, bulletAppState);
        playerCharacter = world.getPlayerCharacter();

        //NOTE: Render engine
        //INFO: will render the world eventually, does nothing rn
        //new RenderEngine(rootNode, assetManager, bulletAppState);

        // =========================
        // BLOCK INTERACTION
        // =========================

        blockInteraction = new BlockInteractionSystem(world, cam, actionInput);

        // Example: selected block (later this comes from hotbar)
        blockInteraction.setSelectedBlock(Blocks.DIAMOND_BLOCK);
    }

    @Override
    public void simpleUpdate(float tpf) {
        tps();
        timeAccumulator += tpf;

        cam.setLocation(playerCharacter.getPlayerControl().getPhysicsLocation().add(0, 0.6f, 0));

        world.update();

        while (timeAccumulator >= tickTime) {
            tick();
            timeAccumulator -= tickTime;
        }
    }

    private void tick() {
        totalTicks++;

        playerCharacter.tick();
        //entityManager.tick();

        // =========================
        // BLOCK INTERACTION TICK
        // =========================

        blockInteraction.update();
    }

    private void tps() {
        //INFO: ticks are inaccurate with small timeActive, clamped at 20 for the first 10 seconds
        timeActiveSeconds = (System.nanoTime() - initialTime) / 1_000_000_000.0;
        double tps = (Math.floor((10 * totalTicks) / timeActiveSeconds)) / 10;
        if (timeActiveSeconds < 20) tps = Math.clamp(tps, 0, 40);
        tpsText.setText("TPS: " + tps);
    }

    private static void disableWarnings() {
        Logger.getLogger("").setLevel(Level.SEVERE);
        Logger.getLogger("").setLevel(Level.WARNING);
    }
}
