package com.minecraftclone.gui.inventory;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.Main;
import com.minecraftclone.gui.display.InventorySlot;
import com.minecraftclone.gui.display.Slot;
import com.minecraftclone.item.ItemInstance;
import com.minecraftclone.util.UIHelper;
import java.util.ArrayList;
import java.util.List;

public class InventoryGUI {

    //TODO: Create Blockitems
    //IDEA: three textures that are transformed in a way to create an illusion of being 3d (need to be darkened to make them more 3d)
    private FlyByCamera flyByCamera;
    private InputManager inputManager;
    private AssetManager asset;
    private UIHelper uiHelper;

    private Node guiNode;
    private Node inventoryNode, inventoryItemsNode;

    private Texture2D inventoryTexture;
    private Picture inventory;

    private List<InventorySlot> inventorySlots = new ArrayList<>();

    public InventoryGUI(Main main, int scale) {
        this.guiNode = main.getGuiNode();
        this.asset = main.getAssetManager();
        this.flyByCamera = main.getFlyByCamera();
        this.inputManager = main.getInputManager();

        BitmapFont font = main.getguiFont();
        uiHelper = new UIHelper(asset, scale, font);

        //DOES: Create Variables for easier positioning of the HUD elements
        int windowWidth = main.getCamera().getWidth();
        int windowHeight = main.getCamera().getHeight();
        int halfWidth = main.getViewPort().getCamera().getWidth() / 2;
        int halfHeight = main.getViewPort().getCamera().getHeight() / 2;

        //DOES: Create Nodes for layering and attach them
        inventoryItemsNode = new Node("inventoryItemsNode");
        inventoryNode = new Node("inventoryNode");

        guiNode.attachChild(inventoryItemsNode);
        inventoryItemsNode.attachChild(inventoryNode);

        //DOES: Create Texture variables
        inventoryTexture = uiHelper.loadGUITexture2d("container/inventory"); //256x256 (176x166) //Info: For some reason the inventory texture file is larger than it needs to be

        //DOES: Create the inventory and position it in the screens center
        inventory = uiHelper.createPicture(inventoryTexture, "inventory");
        inventory.setPosition(halfWidth - (inventory.getWidth() / 2) + 40 * scale, halfHeight - (inventory.getHeight() / 2) - 45 * scale);
        inventoryNode.attachChild(inventory);

        setInventoryVisibility(true);

        //TODO: Clean up magic Numbers (maybe define as constants)
        //DOES: Create invisible Textures on top of the item slots in the inventory so they can be replaced by textures of different items
        for (int i = 0; i < 4; i++) {
            for (int i0 = 0; i0 < 9; i0++) {
                if (i == 0) {
                    InventorySlot slot = new InventorySlot(
                        uiHelper,
                        (windowWidth - inventory.getWidth()) / 2 + scale * (48 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - 203 * scale
                    );
                    slot.attachTo(inventoryItemsNode);
                    inventorySlots.add(slot);
                } else {
                    InventorySlot slot = new InventorySlot(
                        uiHelper,
                        (windowWidth - inventory.getWidth()) / 2 + scale * (48 + 18 * i0),
                        (windowHeight + inventory.getHeight()) / 2 - scale * (127 + 18 * i)
                    );
                    slot.attachTo(inventoryItemsNode);
                    inventorySlots.add(slot);
                }
            }
        }
    }

    /**
     * Changes the visibility of the Inventory. Also makes the Cursor moveable
     * @param visible Specifies the visibility to be either true or false
     */
    public void setInventoryVisibility(boolean visible) {
        if (visible) {
            inventoryItemsNode.setCullHint(Spatial.CullHint.Inherit);
        } else {
            inventoryItemsNode.setCullHint(Spatial.CullHint.Always);
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
    public void displayItem(int row, int column, ItemInstance item) {
        if (row >= 1 && row <= 4) {
            if (column >= 1 && column <= 9) {
                Slot slot = inventorySlots.get(column - 1 + 9 * (row - 1));

                if (item.getStackSize() > 1) {
                    slot.setText(String.valueOf(item.getStackSize()));
                } else {
                    slot.setText("");
                }
                slot.setTexture(uiHelper.loadItemTexture2d(item.getId()));
            }
        }
    }

    public List<InventorySlot> getInventorySlots() {
        return inventorySlots;
    }
}
