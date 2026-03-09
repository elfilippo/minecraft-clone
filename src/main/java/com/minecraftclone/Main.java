package com.minecraftclone;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.minecraftclone.block.Blocks;
import com.minecraftclone.player.PlayerCharacter;
import com.minecraftclone.player.input.ActionInput;
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
    private BlockInteractionSystem blockInteraction;

    public static void main(String[] args) {
        setDefaultSettings();

        Main app = new Main();
        app.setSettings(settings);
        app.start();
    }

    @Override
    protected BitmapFont loadGuiFont() {
        return this.assetManager.loadFont("font/36px-s.fnt");
    }

    @Override
    public void simpleInitApp() {
        initialTime = System.nanoTime();
        tickTime = 1f / TICKS_PER_SECOND;

        //DOES: render tps on screen
        tpsText = new BitmapText(guiFont);
        guiNode.attachChild(tpsText);
        tpsText.setLocalTranslation(0, cam.getHeight(), 0);

        fpsText.setLocalScale(0.5f);

        //NOTE: physics object is bulletAppState
        var bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setAccuracy(1f / TICKS_PER_SECOND);

        //DOES: disable default & set up custom camera
        flyCam.setEnabled(false);
        flyCam.unregisterInput();
        flyCam = new CustomCam(cam);
        flyCam.registerWithInput(inputManager);
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(0f);

        cam.setFrustumNear(0.2f);
        cam.setFov(70);
        getRenderer().setDefaultAnisotropicFilter(4);

        Spatial sky = SkyFactory.createSky(
            assetManager,
            "textures/environment/skybox.png",
            SkyFactory.EnvMapType.EquirectMap
        );
        rootNode.attachChild(sky);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 1f));
        rootNode.addLight(ambient);

        DirectionalLightShadowRenderer shadows = new DirectionalLightShadowRenderer(assetManager, 16384, 1);
        shadows.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        shadows.setLight(sun);
        shadows.setShadowIntensity(0.7f);
        shadows.setEnabledStabilization(true);
        viewPort.addProcessor(shadows);

        //INFO: for all bool inputs (keypresses etc.)
        actionInput = new ActionInput(inputManager);

        //INFO: only for inputs with amounts (mouse movement)
        new KeyMapping(inputManager, actionInput);

        //DOES: set up world and player
        //INFO: world owns all data
        world = new World(this, actionInput, bulletAppState);
        playerCharacter = world.getPlayerCharacter();

        //DOES: nothing rn, will render world eventually
        //new RenderEngine(rootNode, assetManager, bulletAppState);

        //DOES: raycast and break & place blocks
        blockInteraction = new BlockInteractionSystem(world, actionInput, world.getPlayerGui(), this);

        //NOTE: will be set by hotbar later
        blockInteraction.setSelectedBlock(Blocks.DIAMOND_BLOCK);
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

        //DOES: queue & process missing chunks
        world.update();

        //DOES: ticks unbound from framerate
        timeAccumulator += tpf;
        while (timeAccumulator >= tickTime) {
            tick();
            timeAccumulator -= tickTime;
        }

        //DOES: set the camera to the eye height of player's interpolated pos
        //INFO: player pos is interpolated for smoother movement, but tick-dependent coords
        float alpha = timeAccumulator / tickTime;
        Vector3f pInterpolatedPos = playerCharacter.getInterpolatedPosition(alpha);
        cam.setLocation(pInterpolatedPos.add(0, PlayerCharacter.EYE_OFFSET, 0));
    }

    private void tick() {
        //NOTE: all tickables are called here

        //DOES: used for tps calculation
        totalTicks++;

        //DOES: update player (movement etc.)
        playerCharacter.tick(actionInput.buildCommand(), actionInput.getCursorPosition());

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

    public BitmapFont getguiFont() {
        return guiFont;
    }

    //DOES: override jmonkeyengine destroy call to call world.shutdown() as well so that all threads exit
    @Override
    public void destroy() {
        world.shutdown();
        super.destroy();
    }

    public void setPaused(boolean pause) {
        paused = pause;
    }
}
