package com.minecraftclone.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.util.TextureManager;
import java.io.IOException;

public class PlayerGUI {

    private int selectedSlot = 1;
    private int scale; //USAGE: only even numbers
    private Picture hotbar, hotbarSelector, inventory, crosshair, experienceBarEmpty, heartContainer, fullHeart, halfHeart, hungerContainer;
    private Picture fullHunger, halfHunger;
    private int windowWidth, windowHeight;
    private Node guiNode, inventoryNode, containerNode;
    private AssetManager assetManager;
    private Texture2D hotbarTexture, hotbarSelectorTexture, crosshairTexture, inventoryTexture, experienceBarEmptyTexture, heartContainerTexture;
    private Texture2D fullHeartTexture, halfHeartTexture, hungerContainerTexture, fullHungerTexture, halfHungerTexture;
    private Node hungerNode, heartNode, hotbarNode;
    private SimpleApplication app;

    public PlayerGUI(SimpleApplication app) throws IOException {
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

        //Does: Create different Elements of the HUD
        inventory = TextureManager.createPicture(assetManager, inventoryTexture, "inventory", scale);
        hotbar = TextureManager.createPicture(assetManager, hotbarTexture, "hotbar", scale);
        hotbarSelector = TextureManager.createPicture(assetManager, hotbarSelectorTexture, "hotbarSelector", scale);
        experienceBarEmpty = TextureManager.createPicture(assetManager, experienceBarEmptyTexture, "experienceBarEmpty", scale);
        crosshair = TextureManager.createPicture(assetManager, crosshairTexture, "crosshair", scale);

        //DOES: set position of HUD elements
        inventory.setPosition(
            windowWidth / 2 - (((inventory.getWidth() - (80 * scale)) / 2)),
            windowHeight / 2 - (inventory.getHeight() - (90 * scale))
        ); //Info: Inventory not attached so not visible
        hotbar.setPosition(windowWidth / 2 - (hotbar.getWidth() / 2), 0);
        hotbarSelector.setPosition(windowWidth / 2 - ((hotbarSelector.getWidth() / 2)), 0);
        experienceBarEmpty.setPosition(windowWidth / 2 - ((experienceBarEmpty.getWidth() / 2)), hotbar.getHeight() + scale * 2);
        crosshair.setPosition(windowWidth / 2 - ((crosshair.getWidth() / 2)), windowHeight / 2 - ((crosshair.getHeight() / 2)));

        //Does: Attach HUD Elemtents to GUI Node
        hotbarNode.attachChild(hotbar);
        hotbarNode.attachChild(hotbarSelector);
        hotbarNode.attachChild(experienceBarEmpty);
        hotbarNode.attachChild(crosshair);

        //Does: Create Containers for Life and Hunger
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
        //Does: Create Heart Textures on top of the Heart Container (2= full heart, 1 = half heart)
        heartNode.detachAllChildren();
        if (life % 2 == 0) {
            for (int i = 0; i < life / 2; i++) {
                fullHeart = new Picture("fullHeart");
                fullHeart.setTexture(assetManager, fullHeartTexture, true);
                fullHeart.setWidth(9 * scale);
                fullHeart.setHeight(9 * scale);
                fullHeart.setPosition(
                    windowWidth / 2 + 8 * scale * i - hotbar.getWidth() / 2,
                    experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
                );
                heartNode.attachChild(fullHeart);
            }
        } else {
            int i;
            for (i = 0; i < (life - 1) / 2; i++) {
                fullHeart = new Picture("fullHeart");
                fullHeart.setTexture(assetManager, fullHeartTexture, true);
                fullHeart.setWidth(9 * scale);
                fullHeart.setHeight(9 * scale);
                fullHeart.setPosition(
                    windowWidth / 2 + 8 * scale * i - hotbar.getWidth() / 2,
                    experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
                );
                heartNode.attachChild(fullHeart);
            }
            halfHeart = new Picture("halfHeart");
            halfHeart.setTexture(assetManager, halfHeartTexture, true);
            halfHeart.setWidth(9 * scale);
            halfHeart.setHeight(9 * scale);
            halfHeart.setPosition(
                windowWidth / 2 + 8 * scale * i - hotbar.getWidth() / 2,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            heartNode.attachChild(halfHeart);
        }
    }

    public void setHunger(int hunger) {
        //Does: Creates Hunger Textures on top of the Hunger Container (2= full Hunger, 1 = half Hunger)
        hungerNode.detachAllChildren();
        if (hunger % 2 == 0) {
            for (int i = 0; i < hunger / 2; i++) {
                fullHunger = new Picture("fullHunger");
                fullHunger.setTexture(assetManager, fullHungerTexture, true);
                fullHunger.setWidth(9 * scale);
                fullHunger.setHeight(9 * scale);
                fullHunger.setPosition(
                    windowWidth / 2 + hotbar.getWidth() / 2 - 8 * scale * i - 9 * scale,
                    experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
                );
                hungerNode.attachChild(fullHunger);
            }
        } else {
            int i;
            for (i = 0; i < (hunger - 1) / 2; i++) {
                fullHunger = new Picture("fullHunger");
                fullHunger.setTexture(assetManager, fullHungerTexture, true);
                fullHunger.setWidth(9 * scale);
                fullHunger.setHeight(9 * scale);
                fullHunger.setPosition(
                    windowWidth / 2 + hotbar.getWidth() / 2 - 8 * scale * i - 9 * scale,
                    experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
                );
                hungerNode.attachChild(fullHunger);
            }
            halfHunger = new Picture("halfHunger");
            halfHunger.setTexture(assetManager, halfHungerTexture, true);
            halfHunger.setWidth(9 * scale);
            halfHunger.setHeight(9 * scale);
            halfHunger.setPosition(
                windowWidth / 2 + hotbar.getWidth() / 2 - 8 * scale * i - 9 * scale,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hungerNode.attachChild(halfHunger);
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
