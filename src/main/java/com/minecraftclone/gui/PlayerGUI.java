package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.Main;
import com.minecraftclone.util.TextureManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerGUI {

    private Main app;
    private AssetManager assetManager;
    private int windowWidth, windowHeight;
    private int scale; //USAGE: only even numbers

    private int selectedSlot = 1;

    private Texture2D hotbarTexture, hotbarSelectorTexture, crosshairTexture, inventoryTexture, experienceBarEmptyTexture, heartContainerTexture, fullHeartTexture, halfHeartTexture, hungerContainerTexture, fullHungerTexture, halfHungerTexture, blankTexture;
    private Picture hotbar, hotbarSelector, inventory, crosshair, experienceBarEmpty, heartContainer, heart, hungerContainer, hunger;
    private Node guiNode, inventoryNode, containerNode, hungerNode, heartNode, hotbarNode;

    private List<Picture> hearts = new ArrayList<>();
    private List<Picture> hungerBars = new ArrayList<>();

    public PlayerGUI(Main app) throws IOException {
        this.windowWidth = app.getViewPort().getCamera().getWidth();
        this.windowHeight = app.getViewPort().getCamera().getHeight();
        this.app = app;

        //DOES: Autoscale
        int scaleWidth = Math.round(windowWidth / 480);
        int scaleHeight = Math.round(windowHeight / 270);
        scale = (scaleWidth + scaleHeight) / 2;

        guiNode = app.getGuiNode();
        assetManager = app.getAssetManager();

        //Does: Create different Nodes for different parts of the HUD
        inventoryNode = new Node("inventoryNode");
        hotbarNode = new Node("hotbarNode");
        containerNode = new Node("containerNode");
        hungerNode = new Node("hungerNode");
        heartNode = new Node("heartNode");
        guiNode.attachChild(inventoryNode);
        guiNode.attachChild(hotbarNode);
        hotbarNode.attachChild(containerNode);
        hotbarNode.attachChild(hungerNode);
        hotbarNode.attachChild(heartNode);

        //Does: Create Textures
        hotbarTexture = TextureManager.getGuiTexture("/sprites/hud/hotbar"); //182x22
        hotbarSelectorTexture = TextureManager.getGuiTexture("/sprites/hud/hotbar_selection"); //24x23
        crosshairTexture = TextureManager.getGuiTexture("/sprites/hud/crosshair"); //15x15
        inventoryTexture = TextureManager.getGuiTexture("/container/inventory"); //256x256 (176x166) //Info: For some reason the inventory texture file is larger than it needs to be
        experienceBarEmptyTexture = TextureManager.getGuiTexture("/sprites/hud/experience_bar_background"); //182x5
        heartContainerTexture = TextureManager.getGuiTexture("/sprites/hud/heart/container"); //9x9
        fullHeartTexture = TextureManager.getGuiTexture("/sprites/hud/heart/full"); //9x9
        halfHeartTexture = TextureManager.getGuiTexture("/sprites/hud/heart/half"); //9x9
        hungerContainerTexture = TextureManager.getGuiTexture("/sprites/hud/food_empty"); //9x9
        fullHungerTexture = TextureManager.getGuiTexture("/sprites/hud/food_full"); //9x9
        halfHungerTexture = TextureManager.getGuiTexture("/sprites/hud/food_half"); //9x9
        blankTexture = TextureManager.getGuiTexture("/blank"); //1x1

        //Does: Create different Elements of the HUD
        inventory = TextureManager.createPicture(assetManager, inventoryTexture, "inventory", scale);
        hotbar = TextureManager.createPicture(assetManager, hotbarTexture, "hotbar", scale);
        hotbarSelector = TextureManager.createPicture(assetManager, hotbarSelectorTexture, "hotbarSelector", scale);
        experienceBarEmpty = TextureManager.createPicture(assetManager, experienceBarEmptyTexture, "experienceBarEmpty", scale);
        crosshair = TextureManager.createPicture(assetManager, crosshairTexture, "crosshair", scale);

        //DOES: Set position of HUD elements
        inventory.setPosition(
            windowWidth / 2 - (((inventory.getWidth() - (80 * scale)) / 2)),
            windowHeight / 2 - (inventory.getHeight() - (90 * scale))
        );
        hotbar.setPosition(windowWidth / 2 - (hotbar.getWidth() / 2), 0);
        hotbarSelector.setPosition(windowWidth / 2 - ((hotbarSelector.getWidth() / 2)), 0);
        experienceBarEmpty.setPosition(windowWidth / 2 - ((experienceBarEmpty.getWidth() / 2)), hotbar.getHeight() + scale * 2);
        crosshair.setPosition(windowWidth / 2 - ((crosshair.getWidth() / 2)), windowHeight / 2 - ((crosshair.getHeight() / 2)));

        //Does: Attach HUD Elements to GUI Node
        hotbarNode.attachChild(hotbar);
        hotbarNode.attachChild(hotbarSelector);
        hotbarNode.attachChild(experienceBarEmpty);
        hotbarNode.attachChild(crosshair);

        //Does: Create hearts and hungerbars and their containers
        for (int i = 0; i < 10; i++) {
            heartContainer = TextureManager.createPicture(assetManager, heartContainerTexture, "heartContainer", scale);
            heartContainer.setPosition(
                windowWidth / 2 - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            containerNode.attachChild(heartContainer);
        }

        for (int i = 0; i < 10; i++) {
            hungerContainer = TextureManager.createPicture(assetManager, hungerContainerTexture, "hungerContainer", scale);
            hungerContainer.setPosition(
                windowWidth / 2 + 10 * scale + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            containerNode.attachChild(hungerContainer);
        }

        for (int i = 0; i < 10; i++) {
            heart = TextureManager.createPicture(assetManager, fullHeartTexture, "fullHeart", scale);
            heart.setPosition(
                windowWidth / 2 - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hearts.add(heart);
            heartNode.attachChild(heart);
        }

        for (int i = 0; i < 10; i++) {
            hunger = TextureManager.createPicture(assetManager, fullHungerTexture, "hunger", scale);
            hunger.setPosition(
                windowWidth / 2 + hotbar.getWidth() / 2 - 8 * scale * i - 9 * scale,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hungerBars.add(hunger);
            hungerNode.attachChild(hunger);
        }

        changeHotbarSlot(selectedSlot);
    }

    public void changeHotbarSlot(int slot) {
        //Does: Change the Hotbarslot based of the int slot
        if (slot <= 9 && slot >= 1) {
            selectedSlot = slot;
            hotbarSelector.setPosition(
                windowWidth / 2 -
                    ((hotbar.getWidth() / 2) + 1 * scale) -
                    (hotbar.getWidth() - 2 * scale) / 9 +
                    (((hotbar.getWidth() - 2 * scale) / 9) * slot),
                0
            );
        }
    }

    public void setInventoryVisibility(boolean visibility) {
        //Does: set the Visibility of the Inventory
        if (visibility) {
            guiNode.attachChild(inventory);
        } else {
            inventory.removeFromParent();
        }
        app.getInputManager().setCursorVisible(visibility);
        app.getFlyByCamera().setEnabled(!visibility);
        //setHotbarVisibility(!visibility);
    }

    public void setLife(int life) {
        //Does: Changes the heart textures in order to display the players life
        int fullHearts = life / 2;
        boolean hasHalfHeart = (life % 2 == 1);

        for (int i = 0; i < hearts.size(); i++) {
            Picture heart = hearts.get(i);

            if (i < fullHearts) {
                heart.setTexture(assetManager, fullHeartTexture, true);
            } else if (i == fullHearts && hasHalfHeart) {
                heart.setTexture(assetManager, halfHeartTexture, true);
            } else {
                heart.setTexture(assetManager, blankTexture, true);
            }
        }
    }

    public void setHunger(int hunger) {
        //Does: Changes the hunger textures in order to display the players hunger
        int fullHunger = hunger / 2;
        boolean hasHalfHunger = (hunger % 2 == 1);

        for (int i = 0; i < hungerBars.size(); i++) {
            Picture hungerBar = hungerBars.get(i);

            if (i < fullHunger) {
                hungerBar.setTexture(assetManager, fullHungerTexture, true);
            } else if (i == fullHunger && hasHalfHunger) {
                hungerBar.setTexture(assetManager, halfHungerTexture, true);
            } else {
                hungerBar.setTexture(assetManager, blankTexture, true);
            }
        }
    }

    private void setHotbarVisibility(boolean visibility) {
        if (visibility) {
            guiNode.attachChild(hotbarNode);
        } else {
            hotbarNode.removeFromParent();
        }
    }
}
