package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
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
    private Node guiNode, inventoryNode, containerNode;
    private boolean inventoryShown;
    private AssetManager assetManager;
    private Texture2D hotbarTexture, hotbarSelectorTexture, crosshairTexture, inventoryTexture, experienceBarEmptyTexture, heartContainerTexture, fullHeartTexture, halfHeartTexture, hungerContainerTexture, fullHungerTexture, halfHungerTexture;
    private Node hungerNode, heartNode;

    List<ItemInstance> slots = new ArrayList<>(9);

    public PlayerGUI(AppSettings settings, Node guiNode, AssetManager assetManager) throws IOException {
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        inventoryNode = new Node("inventoryNode");
        guiNode.attachChild(inventoryNode);
        containerNode = new Node("contaierNode");
        inventoryNode.attachChild(containerNode);
        hungerNode = new Node("hungerNode");
        heartNode = new Node("heartNode");
        guiNode.attachChild(hungerNode);
        guiNode.attachChild(heartNode);

        windowWidth = settings.getWidth();
        windowHeight = settings.getHeight();

        hotbarTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/hotbar.png")); //182x22
        hotbarSelectorTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/hotbar_selection.png")); //24x23
        crosshairTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/crosshair.png")); //15x15
        inventoryTexture = imageLoader.loadTexture2D(guiPath("container/inventory.png")); //256x256
        experienceBarEmptyTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/experience_bar_background.png")); //182x5
        heartContainerTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/heart/container.png")); //9x9
        fullHeartTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/heart/full.png")); //9x9
        halfHeartTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/heart/half.png")); //9x9
        hungerContainerTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/food_empty.png")); //9x9
        fullHungerTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/food_full.png")); //9x9
        halfHungerTexture = imageLoader.loadTexture2D(guiPath("sprites/hud/food_half.png")); //9x9

        hotbar = new Picture("hotbar");
        hotbar.setTexture(assetManager, hotbarTexture, true);
        hotbar.setWidth(182 * scale);
        hotbar.setHeight(22 * scale);
        hotbar.setPosition(windowWidth / 2 - (hotbar.getWidth() / 2), 0);
        inventoryNode.attachChild(hotbar);

        hotbarSelector = new Picture("hotbarSelector");
        hotbarSelector.setTexture(assetManager, hotbarSelectorTexture, true);
        hotbarSelector.setWidth(24 * scale);
        hotbarSelector.setHeight(23 * scale);
        hotbarSelector.setPosition(windowWidth / 2 - ((hotbarSelector.getWidth() / 2)), 0);
        inventoryNode.attachChild(hotbarSelector);

        experienceBarEmpty = new Picture("experienceBarEmpty");
        experienceBarEmpty.setTexture(assetManager, experienceBarEmptyTexture, true);
        experienceBarEmpty.setWidth(182 * scale);
        experienceBarEmpty.setHeight(5 * scale);
        experienceBarEmpty.setPosition(windowWidth / 2 - ((experienceBarEmpty.getWidth() / 2)), hotbar.getHeight() + scale * 2);
        inventoryNode.attachChild(experienceBarEmpty);

        for (int i = 0; i < 10; i++) {
            heartContainer = new Picture("heartContainer");
            heartContainer.setTexture(assetManager, heartContainerTexture, true);
            heartContainer.setWidth(9 * scale);
            heartContainer.setHeight(9 * scale);
            heartContainer.setPosition(
                windowWidth / 2 - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            containerNode.attachChild(heartContainer);
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
            containerNode.attachChild(hungerContainer);
        }

        crosshair = new Picture("crosshair");
        crosshair.setTexture(assetManager, crosshairTexture, true);
        crosshair.setWidth(15 * scale);
        crosshair.setHeight(15 * scale);
        crosshair.setPosition(windowWidth / 2 - ((crosshair.getWidth() / 2)), windowHeight / 2 - ((crosshair.getHeight() / 2)));
        inventoryNode.attachChild(crosshair);

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

    public boolean toggleInventory() {
        if (!inventoryShown) {
            guiNode.attachChild(inventory);
            inventoryShown = true;
        } else {
            inventory.removeFromParent();
            inventoryShown = false;
        }
        return inventoryShown;
    }

    public void setLife(int life) {
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

    private String guiPath(String path) {
        return "src/main/resources/textures/gui/" + path;
    }
}
