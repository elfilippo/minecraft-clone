package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
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

    private List<Picture> hearts = new ArrayList<>(),
        hungerBars = new ArrayList<>(),
        hotbarList = new ArrayList<>(),
        inventoryList = new ArrayList<>();
    private List<BitmapText> inventoryItemCountList = new ArrayList<>(),
        hotbarItemCountList = new ArrayList<>();
    private List<Vector3f> inventoryTextAnchor = new ArrayList<>();

    private TextureManager textureManager;
    private BitmapFont font;

    public PlayerGUI(Main game) throws IOException {
        //Does: Gets the window resolution
        this.windowWidth = game.getViewPort().getCamera().getWidth();
        this.windowHeight = game.getViewPort().getCamera().getHeight();
        this.game = game;

        //DOES: Autoscale for HUD elements based on screen resolution
        int scaleWidth = Math.round(windowWidth / 480f);
        int scaleHeight = Math.round(windowHeight / 270f);
        scale = (scaleWidth + scaleHeight) / 2;

        //Does: Gets some things from Main and initialize the Texturemanager
        guiNode = game.getGuiNode();
        font = game.getguiFont();
        assetManager = game.getAssetManager();
        textureManager = new TextureManager(assetManager, scale);

        //Does: Create different Nodes for different parts of the HUD
        inventoryItemsNode = new Node("inventoryItemsNode");
        inventoryNode = new Node("inventoryNode");
        hotbarNode = new Node("hotbarNode");
        containerNode = new Node("containerNode");
        hungerNode = new Node("hungerNode");
        heartNode = new Node("heartNode");

        //Does: Attaches the Nodes to the guiNode so they are visible
        guiNode.attachChild(hotbarNode);
        hotbarNode.attachChild(containerNode);
        hotbarNode.attachChild(hungerNode);
        hotbarNode.attachChild(heartNode);
        inventoryItemsNode.attachChild(inventoryNode);

        //Does: Create Textures variables for HUD elements
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

        //Does: Create different Elements of the HUD as Picture objects
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

        //Does: Attach HUD Elements to GUI Node, so they are visible
        inventoryNode.attachChild(inventory);
        hotbarNode.attachChild(hotbar);
        hotbarNode.attachChild(hotbarSelector);
        hotbarNode.attachChild(experienceBarEmpty);
        hotbarNode.attachChild(crosshair);

        //Does: Create hearts and hungerbars and their containers, set their position and attach them to their Nodes
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

        //Does: Generate invisible Pictures and invisible text on top of inventory slots to be replaced with visible items
        for (int i = 0; i < 4; i++) {
            for (int i0 = 0; i0 < 9; i0++) {
                if (i == 0) {
                    BitmapText text = new BitmapText(font);
                    text.setLocalTranslation(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (65 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - 204 * scale + text.getHeight(),
                        0
                    );

                    inventoryItemsNode.attachChild(text);
                    inventoryItemCountList.add(text);
                    inventoryTextAnchor.add(text.getLocalTranslation().clone());

                    Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale); //Usage: Customscale needs to be multiplied by scale otherwise it breaks scalability
                    slot.setPosition(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (48 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - 203 * scale
                    );
                    inventoryItemsNode.attachChild(slot);
                    inventoryList.add(slot);
                } else {
                    BitmapText text = new BitmapText(font);
                    text.setLocalTranslation(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (65 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - scale * (128 + 18 * i) + text.getHeight(),
                        0
                    );

                    inventoryItemsNode.attachChild(text);
                    inventoryItemCountList.add(text);
                    inventoryTextAnchor.add(text.getLocalTranslation().clone());

                    Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale); //Usage: Customscale needs to be multiplied by scale otherwise it breaks scalability
                    slot.setPosition(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (48 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - scale * (127 + 18 * i)
                    );
                    inventoryItemsNode.attachChild(slot);
                    inventoryList.add(slot);
                }
            }
        }

        //Does: Generate invisible Pictures on top of hotbar slots to be replaced with visible items
        for (int i = 0; i < 9; i++) {
            BitmapText text = new BitmapText(font);
            text.setLocalTranslation((windowWidth - hotbar.getWidth()) / 2 + scale * (20 + 20 * i), 2 * scale + text.getHeight(), 0);

            hotbarNode.attachChild(text);
            hotbarItemCountList.add(text);
            inventoryTextAnchor.add(text.getLocalTranslation().clone());

            Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale); //Usage: Customscale needs to be multiplied by scale otherwise it breaks scalability
            slot.setPosition((windowWidth - hotbar.getWidth()) / 2 + scale * (3 + 20 * i), 3 * scale);
            hotbarNode.attachChild(slot);
            hotbarList.add(slot);
        }

        changeHotbarSlot(selectedSlot);
        setInventoryVisibility(false);
    }

    public void changeHotbarSlot(int slot) {
        //Does: Change the Hotbarslot based of the given int slot
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
    }

    public void setLife(int life) {
        //Does: Changes the heart textures in order to display the players life odd numbers make half hearts
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
        //Does: Changes the hunger textures in order to display the players hunger odd numbers make half hunger bars
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
        //Info: The items displayed in the Hotbar are copied from those in the inventoryList, because the inventory also has a Hotbar
        //Does: Checks for differences between the inventory hotbar and real hotbar and if they are not the same displays the item in the inventory hotbar in the hotbar
        for (int i = 0; i < 9; i++) {
            Picture slot = hotbarList.get(i);

            BitmapText text = hotbarItemCountList.get(i);
            Vector3f anchor = inventoryTextAnchor.get(i + 36);

            slot.setMaterial(inventoryList.get(i).getMaterial());

            text.setText(inventoryItemCountList.get(i).getText());
            text.setLocalTranslation(anchor.x - text.getLineWidth(), anchor.y, anchor.z);
        }
    }

    public void inventoryDisplayItem(int row, int column, ItemInstance item) {
        //Does: Shows an item in the inventory row 1 is the hotbar
        if (row >= 1 && row <= 4) {
            if (column >= 1 && column <= 9) {
                Picture slot = inventoryList.get(column - 1 + 9 * (row - 1));
                BitmapText text = inventoryItemCountList.get(column - 1 + 9 * (row - 1));
                Vector3f anchor = inventoryTextAnchor.get(column - 1 + 9 * (row - 1));

                if (!String.valueOf(item.getStackSize()).equals("1")) text.setText(String.valueOf(item.getStackSize()));
                text.setLocalTranslation(anchor.x - text.getLineWidth(), anchor.y, anchor.z);
                slot.setTexture(assetManager, TextureManager.getItemTexture(item.getId()), true);
            }
        }
        updateHotbarDisplayItem();
    }
}
