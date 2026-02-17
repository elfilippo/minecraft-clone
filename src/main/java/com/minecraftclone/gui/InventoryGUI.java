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

    private FlyByCamera flyByCamera;
    private InputManager inputManager;
    private AssetManager asset;
    private TextureManager textureManager;
    private Node guiNode;
    private BitmapFont font;

    private Node inventoryNode, inventoryItemsNode;
    private Texture2D inventoryTexture, blankTexture;
    private Picture inventory;

    private int halfWidth, halfHeight, windowWidth, windowHeight;

    private List<Picture> inventoryList = new ArrayList<>();
    private List<BitmapText> inventoryTextList = new ArrayList<>();
    private List<Vector3f> inventoryTextAnchorList = new ArrayList<>();

    InventoryGUI(Main main, int scale) {
        this.guiNode = main.getGuiNode();
        this.asset = main.getAssetManager();
        font = main.getguiFont();
        flyByCamera = main.getFlyByCamera();
        inputManager = main.getInputManager();

        windowWidth = main.getCamera().getWidth();
        windowHeight = main.getCamera().getHeight();

        halfWidth = main.getViewPort().getCamera().getWidth() / 2;
        halfHeight = main.getViewPort().getCamera().getHeight() / 2;

        textureManager = new TextureManager(asset, scale);

        inventoryItemsNode = new Node("inventoryItemsNode");
        inventoryNode = new Node("inventoryNode");

        inventoryItemsNode.attachChild(inventoryNode);

        inventoryTexture = TextureManager.getGuiTexture("container/inventory"); //256x256 (176x166) //Info: For some reason the inventory texture file is larger than it needs to be
        blankTexture = TextureManager.getGuiTexture("blank"); //1x1

        inventory = textureManager.createPicture(inventoryTexture, "inventory");
        inventory.setPosition(halfWidth - (inventory.getWidth() / 2) + 40 * scale, halfHeight - (inventory.getHeight() / 2) - 45 * scale);
        inventoryNode.attachChild(inventory);

        //TODO: Clean up magic Numbers
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
                    text.setLocalTranslation(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (65 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - 204 * scale + text.getHeight(),
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
                    text.setLocalTranslation(
                        (windowWidth - inventory.getWidth()) / 2 + scale * (65 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - scale * (128 + 18 * i) + text.getHeight(),
                        0
                    );
                    inventoryItemsNode.attachChild(text);
                    inventoryTextList.add(text);
                    inventoryTextAnchorList.add(text.getLocalTranslation().clone());
                }
            }
        }
    }

    void setInventoryVisibility(boolean visibility) {
        //Does: set the Visibility of the Inventory
        if (visibility) {
            guiNode.attachChild(inventoryItemsNode);
        } else {
            guiNode.detachChild(inventoryItemsNode);
        }
        inputManager.setCursorVisible(visibility);
        flyByCamera.setEnabled(!visibility); //Todo: nneds to be changed
    }

    void displayItem(int row, int column, ItemInstance item) {
        //Does: Shows an item in the inventory row 1 is the hotbar
        if (row >= 1 && row <= 4) {
            if (column >= 1 && column <= 9) {
                Picture slot = inventoryList.get(column - 1 + 9 * (row - 1));
                BitmapText text = inventoryTextList.get(column - 1 + 9 * (row - 1));
                Vector3f anchor = inventoryTextAnchorList.get(column - 1 + 9 * (row - 1));

                if (!String.valueOf(item.getStackSize()).equals("1")) text.setText(String.valueOf(item.getStackSize()));
                text.setLocalTranslation(anchor.x - text.getLineWidth(), anchor.y, anchor.z);
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
