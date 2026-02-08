package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.item.ItemInstance;
import com.minecraftclone.util.ImageLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerGUI {

    private int selectedSlot = 1;
    private int scale = 4; // nur glatte zahlen
    private Picture hotbar, hotbarSelector, inventory, crosshair, experienceBarEmpty, heartContainer, fullHeart, halfHeart, hungerContainer, fullHunger, halfHunger;
    private int windowWidth, windowHeight;
    private ImageLoader imageLoader = new ImageLoader();
    private Node guiNode, hungerContainerNode, containerNode, heartContainerNode;
    private boolean inventoryShown;
    private AssetManager assetManager;
    private Texture2D hotbarTexture, hotbarSelectorTexture, crosshairTexture, inventoryTexture, experienceBarEmptyTexture, heartContainerTexture, fullHeartTexture, halfHeartTexture, hungerContainerTexture, fullHungerTexture, halfHungerTexture;

    private Node hungerNode, heartNode;

    List<ItemInstance> slots = new ArrayList<>(9);

    public PlayerGUI(AppSettings settings, Node guiNode, AssetManager assetManager) throws IOException {
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        hungerNode = new Node("hungerNode");
        heartNode = new Node("heartNode");
        guiNode.attachChild(hungerNode);
        guiNode.attachChild(heartNode);

        heartContainerNode = new Node("heartContainerNode");
        heartNode.attachChild(heartContainerNode);
        hungerContainerNode = new Node("hungerContainerNode");
        hungerNode.attachChild(hungerContainerNode);

        windowWidth = settings.getWidth();
        windowHeight = settings.getHeight();

        hotbarTexture = loadTexture(hudPath("hotbar.png")); //182x22
        hotbarSelectorTexture = loadTexture(hudPath("hotbar_selection.png")); //24x23
        crosshairTexture = loadTexture(hudPath("crosshair.png")); //15x15
        inventoryTexture = loadTexture("src/main/resources/textures/gui/container/inventory.png"); //256x256
        experienceBarEmptyTexture = loadTexture(hudPath("experience_bar_background.png")); //182x5
        heartContainerTexture = loadTexture(hudPath("heart/container.png")); //9x9
        fullHeartTexture = loadTexture(hudPath("heart/full.png")); //9x9
        halfHeartTexture = loadTexture(hudPath("heart/half.png")); //9x9
        hungerContainerTexture = loadTexture(hudPath("food_empty.png")); //9x9
        fullHungerTexture = loadTexture(hudPath("food_full.png")); //9x9
        halfHungerTexture = loadTexture(hudPath("food_half.png")); //9x9

        hotbar = new Picture("hotbar");
        hotbar.setTexture(assetManager, hotbarTexture, true);
        hotbar.setWidth(182 * scale);
        hotbar.setHeight(22 * scale);
        hotbar.setPosition(windowWidth / 2 - (hotbar.getWidth() / 2), 0);
        guiNode.attachChild(hotbar);

        hotbarSelector = new Picture("hotbarSelector");
        hotbarSelector.setTexture(assetManager, hotbarSelectorTexture, true);
        hotbarSelector.setWidth(24 * scale);
        hotbarSelector.setHeight(23 * scale);
        hotbarSelector.setPosition(windowWidth / 2 - ((hotbarSelector.getWidth() / 2)), 0);
        guiNode.attachChild(hotbarSelector);

        experienceBarEmpty = new Picture("experienceBarEmpty");
        experienceBarEmpty.setTexture(assetManager, experienceBarEmptyTexture, true);
        experienceBarEmpty.setWidth(182 * scale);
        experienceBarEmpty.setHeight(5 * scale);
        experienceBarEmpty.setPosition(windowWidth / 2 - ((experienceBarEmpty.getWidth() / 2)), hotbar.getHeight() + scale * 2);
        guiNode.attachChild(experienceBarEmpty);

        for (int i = 0; i < 10; i++) {
            heartContainer = new Picture("heartContainer");
            heartContainer.setTexture(assetManager, heartContainerTexture, true);
            heartContainer.setWidth(9 * scale);
            heartContainer.setHeight(9 * scale);
            heartContainer.setPosition(
                windowWidth / 2 - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            heartContainerNode.attachChild(heartContainer);
        }

        for (int i = 0; i < 10; i++) {
            hungerContainer = new Picture("hungerContainer");
            hungerContainer.setTexture(assetManager, hungerContainerTexture, true);
            hungerContainer.setWidth(9 * scale);
            hungerContainer.setHeight(9 * scale);
            hungerContainer.setPosition(
                windowWidth / 2 + 10 * scale + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hungerContainerNode.attachChild(hungerContainer);
        }

        crosshair = new Picture("crosshair");
        crosshair.setTexture(assetManager, crosshairTexture, true);
        crosshair.setWidth(15 * scale);
        crosshair.setHeight(15 * scale);
        crosshair.setPosition(windowWidth / 2 - ((crosshair.getWidth() / 2)), windowHeight / 2 - ((crosshair.getHeight() / 2)));
        guiNode.attachChild(crosshair);

        inventory = new Picture("inventory");
        inventory.setTexture(assetManager, inventoryTexture, true);
        inventory.setWidth(256 * scale);
        inventory.setHeight(256 * scale);
        inventory.setPosition(
            windowWidth / 2 - (((inventory.getWidth() - (80 * scale)) / 2)),
            windowHeight / 2 - (inventory.getHeight() - (90 * scale))
        );

        changeHotbarSlot(selectedSlot);
        inventoryShown = false;
    }

    public void changeHotbarSlot(int slot) {
        //nicht hinterfragen
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

    /*
    public void hotbarSlotUp() {
        changeHotbarSlot(selectedSlot + 1);
    }

    public void hotbarSlotDown() {
        changeHotbarSlot(selectedSlot - 1);
    } 
    */

    public void toggleInventory() {
        if (!inventoryShown) {
            guiNode.attachChild(inventory);
            inventoryShown = true;
        } else {
            inventory.removeFromParent();
            inventoryShown = false;
        }
    }

    private Texture2D loadTexture(String path) throws IOException {
        Texture2D texture = new Texture2D(imageLoader.loadImage(path));
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        texture.setMagFilter(Texture.MagFilter.Nearest);
        return texture;
    }

    public void setLife(int life) {
        for (int i = 0; i < 10; i++) {
            //TODO:
            fullHeart = new Picture("fullHeart");
            fullHeart.setTexture(assetManager, fullHeartTexture, true);
            fullHeart.setWidth(9 * scale);
            fullHeart.setHeight(9 * scale);
            fullHeart.setPosition(
                windowWidth / 2 - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            heartNode.attachChild(fullHeart);
        }
    }

    //TODO:
    public void setHunger(int hunger) {
        /*for (int i = 0; i < 10; i++) {
            hungerContainer = new Picture("hungerContainer");
            hungerContainer.setTexture(assetManager, hungerContainerTexture, true);
            hungerContainer.setWidth(9 * scale);
            hungerContainer.setHeight(9 * scale);
            hungerContainer.setPosition(
                windowWidth / 2 + 10 * scale + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            guiNode.attachChild(hungerContainer);
        }*/

        for (int i = 0; i < 10; i++) {
            fullHunger = new Picture("fullHunger");
            fullHunger.setTexture(assetManager, fullHungerTexture, true);
            fullHunger.setWidth(9 * scale);
            fullHunger.setHeight(9 * scale);
            fullHunger.setPosition(
                windowWidth / 2 + 10 * scale + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hungerNode.attachChild(fullHunger);
        }
        //hungerNode.detachAllChildren();
    }

    private String hudPath(String path) {
        return "src/main/resources/textures/gui/sprites/hud/" + path;
    }
}
