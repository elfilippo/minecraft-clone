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
import com.minecraftclone.render.CustomCam;
import com.minecraftclone.world.BlockInteractionSystem;
import com.minecraftclone.world.World;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends SimpleApplication {

    //INFO: update with new versions
    public static final String VERSION = "v0.3.0-alpha";

    //INFO: disable when debugging
    public static boolean disableWarnings = true;

    //DOES: settings
    public static AppSettings settings;
    public static boolean fullscreen = false;
    public static int screen_width = 1280;
    public static int screen_height = 720;
    private boolean initialized = false;

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
        setDefaultSettings();

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
        flyCam.setEnabled(false);
        flyCam.unregisterInput();

        flyCam = new CustomCam(cam);
        flyCam.registerWithInput(inputManager);

        // Enable mouse look (this also hides + locks cursor correctly)
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(false);
        flyCam.setMoveSpeed(0f); // optional, same as normal FlyCam

        cam.setFrustumNear(0.2f);
        cam.setFov(70);
        inputManager.setCursorVisible(false);
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
        blockInteraction.setSelectedBlock(Blocks.STAIRS);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //DOES: run once and set cursor visibility
        //INFO: is necessary to skip simpleInit because something fucks cursor visibility up there
        //INFO: can be removed later when starting screen is added
        init();

        //DOES: calculate & update tps
        //INFO: update() methods are bound by fps, tick() by tps
        tps();

        //DOES: set the camera to the eye height of player's pos
        cam.setLocation(playerCharacter.getPlayerControl().getPhysicsLocation().add(0, PlayerCharacter.EYE_OFFSET, 0));

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

    private static void setDefaultSettings() {
        //INFO: jmonkeyengine spams warnings for missing optional modules
        //INFO: this disables all warnings that are not critical, disable for debugging
        if (disableWarnings) {
            Logger.getLogger("").setLevel(Level.SEVERE);
        }

        settings = new AppSettings(true);
        settings.setTitle("minecraft-clone " + VERSION + "                  © Mats O. & Filip M.");
        settings.setVSync(true);

        //DOES: set max frame rate (vsync caps at 60 by default)
        settings.setFrequency(100000);

        //DOES: set anti aliasing
        settings.setSamples(4);

        //DOES: set resolution automatically if fullscreen
        if (fullscreen) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            settings.setWindowSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
            settings.setFullscreen(true);
        } else {
            settings.setWindowSize(screen_width, screen_height);
        }
    }

    private void init() {
        if (!initialized) {
            inputManager.setCursorVisible(false);
            initialized = true;
        }
    }
}
