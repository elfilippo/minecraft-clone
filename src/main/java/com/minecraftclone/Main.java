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

    //DOES: tps stuff
    private static final float TICKS_PER_SECOND = 40f;
    private float timeAccumulator;
    private float tickTime;
    private int totalTicks;
    private BitmapText tpsText;
    private long initialTime;
    private double timeActiveSeconds;

    //DOES: objects
    private PlayerCharacter playerCharacter;
    private World world;
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
        tickTime = 1f / TICKS_PER_SECOND;

        //DOES: render tps on screen
        tpsText = new BitmapText(guiFont);
        guiNode.attachChild(tpsText);
        tpsText.setLocalTranslation(10, settings.getHeight() - 20, 0);

        //NOTE: physics object is bulletAppState
        var bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //DOES: set up camera and anisotropic filter
        //NOTE:  had to implement completely custom camera movement for vertical and horizontal clamping
        //NOTE:  which was be much worse so sticking with flyCam for now
        flyCam.setEnabled(true);
        cam.setFrustumNear(0.2f);
        cam.setFov(70);
        getRenderer().setDefaultAnisotropicFilter(4);

        //INFO: for all bool inputs (keypresses etc.)
        actionInput = new ActionInput();

        //INFO: only for inputs with amounts (mouse movement)
        //DOES: nothing rn
        analogInput = new AnalogInput();
        new KeyMapping(inputManager, actionInput, flyCam);

        //DOES: set up world and player
        //INFO: world owns all data
        world = new World(this, actionInput, analogInput, bulletAppState);
        playerCharacter = world.getPlayerCharacter();

        //DOES: nothing rn, will render world eventually
        //new RenderEngine(rootNode, assetManager, bulletAppState);

        //DOES: raycast and break & place blocks
        blockInteraction = new BlockInteractionSystem(world, cam, actionInput);

        //NOTE: will be set by hotbar later
        blockInteraction.setSelectedBlock(Blocks.DIAMOND_BLOCK);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //NOTE: update() methods are bound by fps, tick() by tps

        //DOES: calculate & update tps
        tps();

        //DOES: set the camera to the player's pos
        cam.setLocation(playerCharacter.getPlayerControl().getPhysicsLocation().add(0, 0.6f, 0));

        //DOES: queue & process missing chunks
        world.update();

        //DOES: ticks unbound from framerate
        timeAccumulator += tpf;
        while (timeAccumulator >= tickTime) {
            tick();
            timeAccumulator -= tickTime;
        }
    }

    private void tick() {
        //NOTE: all tickables are called here

        //DOES: used for tps calculation
        totalTicks++;

        //DOES: update player (movement etc.)
        playerCharacter.tick();

        //DOES: update entities
        //NOTE: no entities yet lol
        //entityManager.tick();

        //DOES: check for block interaction
        blockInteraction.tick();
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
