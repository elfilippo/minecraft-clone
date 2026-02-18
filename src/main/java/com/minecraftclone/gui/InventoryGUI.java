package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.Main;
import com.minecraftclone.item.ItemInstance;
import com.minecraftclone.util.TextureManager;
import java.util.ArrayList;
import java.util.List;

class InventoryGUI {

    //TODO: Create Blockitems
    //IDEA: three textures that are transformed in a way to create an illusion of being 3d (need to be darkened to make them more 3d)
    private FlyByCamera flyByCamera;
    private InputManager inputManager;
    private AssetManager asset;

    private Node guiNode;
    private Node inventoryNode, inventoryItemsNode;

    private Texture2D inventoryTexture, blankTexture;
    private Picture inventory;

    private float fontScale;
    private int halfWidth, halfHeight, windowWidth, windowHeight;

    private List<Picture> inventoryList = new ArrayList<>();
    private List<BitmapText> inventoryTextList = new ArrayList<>();
    private List<Vector3f> inventoryTextAnchorList = new ArrayList<>();

    InventoryGUI(Main main, int scale) {
        this.guiNode = main.getGuiNode();
        this.asset = main.getAssetManager();
        this.flyByCamera = main.getFlyByCamera();
        this.inputManager = main.getInputManager();
        this.fontScale = scale / 4f;

        BitmapFont font = main.getguiFont();
        TextureManager textureManager = new TextureManager(asset, scale);

        //DOES: Create Variables for easier positioning of the HUD elements
        windowWidth = main.getCamera().getWidth();
        windowHeight = main.getCamera().getHeight();
        halfWidth = main.getViewPort().getCamera().getWidth() / 2;
        halfHeight = main.getViewPort().getCamera().getHeight() / 2;

        //DOES: Create Nodes for layering and attach them
        inventoryItemsNode = new Node("inventoryItemsNode");
        inventoryNode = new Node("inventoryNode");

        inventoryItemsNode.attachChild(inventoryNode);

        //DOES: Create Texture variables
        inventoryTexture = TextureManager.getGuiTexture("container/inventory"); //256x256 (176x166) //Info: For some reason the inventory texture file is larger than it needs to be
        blankTexture = TextureManager.getGuiTexture("blank"); //1x1

        //DOES: Create the inventory and position it in the screens center
        inventory = textureManager.createPicture(inventoryTexture, "inventory");
        inventory.setPosition(halfWidth - (inventory.getWidth() / 2) + 40 * scale, halfHeight - (inventory.getHeight() / 2) - 45 * scale);
        inventoryNode.attachChild(inventory);

        //TODO: Clean up magic Numbers
        //DOES: Create invisible Textures on top of the item slots in the inventory so they can be replaced by textures of different items
        for (int i = 0; i < 4; i++) {
            for (int i0 = 0; i0 < 9; i0++) {
                if (i == 0) {
                    Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale); //Usage: Customscale needs to be multiplied by scale otherwise it breaks scalability
                    slot.setPosition(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (48 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - 203 * scale
                    );
                    inventoryItemsNode.attachChild(slot);
                    inventoryList.add(slot);

                    BitmapText text = new BitmapText(font);
                    text.setLocalScale(fontScale);
                    text.setLocalTranslation(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (65 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - 204 * scale,
                        0
                    );
                    inventoryItemsNode.attachChild(text);
                    inventoryTextList.add(text);
                    inventoryTextAnchorList.add(text.getLocalTranslation().clone());
                } else {
                    Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale); //Usage: Customscale needs to be multiplied by scale otherwise it breaks scalability
                    slot.setPosition(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (48 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - scale * (127 + 18 * i)
                    );
                    inventoryItemsNode.attachChild(slot);
                    inventoryList.add(slot);

                    BitmapText text = new BitmapText(font);
                    text.setLocalScale(fontScale);
                    text.setLocalTranslation(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (65 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - scale * (128 + 18 * i),
                        0
                    );
                    inventoryItemsNode.attachChild(text);
                    inventoryTextList.add(text);
                    inventoryTextAnchorList.add(text.getLocalTranslation().clone());
                }
            }
        }
    }

    /**
     * Changes the visibility of the Inventory. Also makes the Cursor moveable
     * @param visible Specifies the visibility to be either true or false
     */
    void setInventoryVisibility(boolean visible) {
        if (visible) {
            guiNode.attachChild(inventoryItemsNode);
        } else {
            guiNode.detachChild(inventoryItemsNode);
        }
        inputManager.setCursorVisible(visible);
        flyByCamera.setEnabled(!visible); //Todo: nneds to be changed
    }

    /**
     * Displays an item at the specified slot in the inventory
     * @param row Specifies the row where the item should be displayed
     * @param column Specifies the column where the item should be displayed
     * @param item SPecifies the item that should be displayed
     */
    void displayItem(int row, int column, ItemInstance item) {
        if (row >= 1 && row <= 4) {
            if (column >= 1 && column <= 9) {
                Picture slot = inventoryList.get(column - 1 + 9 * (row - 1));
                BitmapText text = inventoryTextList.get(column - 1 + 9 * (row - 1));
                Vector3f anchor = inventoryTextAnchorList.get(column - 1 + 9 * (row - 1));

                if (!String.valueOf(item.getStackSize()).equals("1")) text.setText(String.valueOf(item.getStackSize()));
                text.setLocalTranslation(anchor.x - text.getLineWidth() * fontScale, anchor.y + text.getHeight() * fontScale, anchor.z);
                slot.setTexture(asset, TextureManager.getItemTexture(item.getId()), true);
            }
        }
    }

    List<Picture> getInventoryList() {
        return inventoryList;
    }

    List<BitmapText> getInventoryTextList() {
        return inventoryTextList;
    }
}
