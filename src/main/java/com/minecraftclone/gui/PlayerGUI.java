package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.Main;
import com.minecraftclone.item.ItemInstance;
import com.minecraftclone.util.TextureManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerGUI {

    private Main game;
    private AssetManager assetManager;
    private int windowWidth, windowHeight;
    private int scale; //USAGE: only even numbers

    private int selectedSlot = 1;

    private Texture2D hotbarTexture, hotbarSelectorTexture, crosshairTexture, inventoryTexture, experienceBarEmptyTexture, heartContainerTexture, fullHeartTexture, halfHeartTexture, hungerContainerTexture, fullHungerTexture, halfHungerTexture, blankTexture;
    private Picture hotbar, hotbarSelector, inventory, crosshair, experienceBarEmpty, heartContainer, heart, hungerContainer, hunger;
    private Node guiNode, inventoryNode, containerNode, hungerNode, heartNode, hotbarNode, inventoryItemsNode;

    private List<Picture> hearts = new ArrayList<>();
    private List<Picture> hungerBars = new ArrayList<>();
    private List<Picture> hotbarList = new ArrayList<>();
    private List<Picture> inventoryList = new ArrayList<>();

    private TextureManager textureManager;

    public PlayerGUI(Main game) throws IOException {
        this.windowWidth = game.getViewPort().getCamera().getWidth();
        this.windowHeight = game.getViewPort().getCamera().getHeight();
        this.game = game;

        //DOES: Autoscale
        int scaleWidth = Math.round(windowWidth / 480);
        int scaleHeight = Math.round(windowHeight / 270);
        scale = (scaleWidth + scaleHeight) / 2;

        guiNode = game.getGuiNode();
        assetManager = game.getAssetManager();
        textureManager = new TextureManager(assetManager, scale);

        //Does: Create different Nodes for different parts of the HUD
        inventoryItemsNode = new Node("inventoryItemsNode");
        inventoryNode = new Node("inventoryNode");
        hotbarNode = new Node("hotbarNode");
        containerNode = new Node("containerNode");
        hungerNode = new Node("hungerNode");
        heartNode = new Node("heartNode");

        guiNode.attachChild(hotbarNode);
        hotbarNode.attachChild(containerNode);
        hotbarNode.attachChild(hungerNode);
        hotbarNode.attachChild(heartNode);
        inventoryItemsNode.attachChild(inventoryNode);

        //Does: Create Textures
        hotbarTexture = TextureManager.getGuiTexture("sprites/hud/hotbar"); //182x22
        hotbarSelectorTexture = TextureManager.getGuiTexture("sprites/hud/hotbar_selection"); //24x23
        crosshairTexture = TextureManager.getGuiTexture("sprites/hud/crosshair"); //15x15
        inventoryTexture = TextureManager.getGuiTexture("container/inventory"); //256x256 (176x166) //Info: For some reason the inventory texture file is larger than it needs to be
        experienceBarEmptyTexture = TextureManager.getGuiTexture("sprites/hud/experience_bar_background"); //182x5
        heartContainerTexture = TextureManager.getGuiTexture("sprites/hud/heart/container"); //9x9
        fullHeartTexture = TextureManager.getGuiTexture("sprites/hud/heart/full"); //9x9
        halfHeartTexture = TextureManager.getGuiTexture("sprites/hud/heart/half"); //9x9
        hungerContainerTexture = TextureManager.getGuiTexture("sprites/hud/food_empty"); //9x9
        fullHungerTexture = TextureManager.getGuiTexture("sprites/hud/food_full"); //9x9
        halfHungerTexture = TextureManager.getGuiTexture("sprites/hud/food_half"); //9x9
        blankTexture = TextureManager.getGuiTexture("blank"); //1x1

        //Does: Create different Elements of the HUD
        inventory = textureManager.createPicture(inventoryTexture, "inventory");
        hotbar = textureManager.createPicture(hotbarTexture, "hotbar");
        hotbarSelector = textureManager.createPicture(hotbarSelectorTexture, "hotbarSelector");
        experienceBarEmpty = textureManager.createPicture(experienceBarEmptyTexture, "experienceBarEmpty");
        crosshair = textureManager.createPicture(crosshairTexture, "crosshair");

        //DOES: Set position of HUD elements
        inventory.setPosition(
            windowWidth / 2 - (inventory.getWidth() / 2) + 40 * scale,
            windowHeight / 2 - (inventory.getHeight() / 2) - 45 * scale
        );
        hotbar.setPosition(windowWidth / 2 - (hotbar.getWidth() / 2), 0);
        hotbarSelector.setPosition(windowWidth / 2 - ((hotbarSelector.getWidth() / 2)), 0);
        experienceBarEmpty.setPosition(windowWidth / 2 - ((experienceBarEmpty.getWidth() / 2)), hotbar.getHeight() + scale * 2);
        crosshair.setPosition(windowWidth / 2 - ((crosshair.getWidth() / 2)), windowHeight / 2 - ((crosshair.getHeight() / 2)));

        //Does: Attach HUD Elements to GUI Node
        inventoryNode.attachChild(inventory);
        hotbarNode.attachChild(hotbar);
        hotbarNode.attachChild(hotbarSelector);
        hotbarNode.attachChild(experienceBarEmpty);
        hotbarNode.attachChild(crosshair);

        //Does: Create hearts and hungerbars and their containers
        for (int i = 0; i < 10; i++) {
            heartContainer = textureManager.createPicture(heartContainerTexture, "heartContainer");
            heartContainer.setPosition(
                windowWidth / 2 - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            containerNode.attachChild(heartContainer);

            hungerContainer = textureManager.createPicture(hungerContainerTexture, "hungerContainer");
            hungerContainer.setPosition(
                windowWidth / 2 + 10 * scale + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            containerNode.attachChild(hungerContainer);

            heart = textureManager.createPicture(fullHeartTexture, "fullHeart");
            heart.setPosition(
                windowWidth / 2 - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hearts.add(heart);
            heartNode.attachChild(heart);

            hunger = textureManager.createPicture(fullHungerTexture, "hunger");
            hunger.setPosition(
                windowWidth / 2 + hotbar.getWidth() / 2 - 8 * scale * i - 9 * scale,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hungerBars.add(hunger);
            hungerNode.attachChild(hunger);
        }

        for (int i = 0; i < 4; i++) {
            for (int i0 = 0; i0 < 9; i0++) {
                if (i == 0) {
                    Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale);
                    slot.setPosition(
                        windowWidth / 2 - (inventory.getWidth() - 80 * scale) / 2 + 8 * scale + 18 * scale * i0,
                        windowHeight / 2 + (inventory.getHeight() - 90 * scale) / 2 - 100 * scale - 3 * scale * 18 - 4 * scale
                    );
                    inventoryItemsNode.attachChild(slot);
                    inventoryList.add(slot);
                } else {
                    Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale); //Usage: Customscale needs to be multiplied by scale otherwise it breaks scalability
                    slot.setPosition(
                        windowWidth / 2 - (inventory.getWidth() - 80 * scale) / 2 + 8 * scale + 18 * scale * i0,
                        windowHeight / 2 + (inventory.getHeight() - 90 * scale) / 2 - 100 * scale - (i - 1) * scale * 18
                    );
                    inventoryItemsNode.attachChild(slot);
                    inventoryList.add(slot);
                }
            }
        }

        //Does: Creates invisible textures the size of an item in the hotbar to be replaced
        for (int i = 0; i < 9; i++) {
            Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale); //Usage: Customscale needs to be multiplied by scale otherwise it breaks scalability
            slot.setPosition(windowWidth / 2 - hotbar.getWidth() / 2 + 3 * scale + 20 * scale * i, 3 * scale);
            hotbarNode.attachChild(slot);
            hotbarList.add(slot);
        }

        changeHotbarSlot(selectedSlot);
        setInventoryVisibility(false);
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
            guiNode.attachChild(inventoryItemsNode);
        } else {
            guiNode.detachChild(inventoryItemsNode);
        }
        game.getInputManager().setCursorVisible(visibility);
        game.getFlyByCamera().setEnabled(!visibility);
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

    private void updateHotbarDisplayItem() {
        for (int i = 0; i < 9; i++) {
            if (hotbarList.get(i).getMaterial() != inventoryList.get(i).getMaterial()) {
                Picture slot = hotbarList.get(i);
                slot.setMaterial(inventoryList.get(i).getMaterial());
            }
        }
    }

    public void inventoryDisplayItem(int row, int column, ItemInstance item) {
        if (row >= 1 && row <= 4) {
            if (column >= 1 && column <= 9) {
                Picture slot = inventoryList.get(column - 1 + 9 * (row - 1));
                slot.setTexture(assetManager, TextureManager.getItemTexture(item.getId()), true);
            }
        }
        updateHotbarDisplayItem();
    }
}
