package com.minecraftclone.gui.menu.inventory;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.minecraftclone.Main;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.Display;
import com.minecraftclone.gui.display.InventorySlot;
import com.minecraftclone.gui.display.Slot;
import com.minecraftclone.item.ItemInstance;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    //TODO: Create Blockitems
    //IDEA: three textures that are transformed in a way to create an illusion of being 3d (need to be darkened to make them more 3d)
    private FlyByCamera flyByCamera;
    private InputManager inputManager;
    private GUIManager guiManager;

    private Node guiNode;
    private Node inventoryNode;

    private static final int OFFSETX = 40;
    private static final int OFFSETY = 45;

    public Inventory(Main main, GUIManager guiManager) {
        this.guiManager = guiManager;
        this.guiNode = guiManager.getGuiNode();

        this.flyByCamera = main.getFlyByCamera();
        this.inputManager = main.getInputManager();

        //DOES: Creates Nodes and attaches them
        inventoryNode = new Node("inventoryNode");
        guiNode.attachChild(inventoryNode);

        //DOES: Create the inventory and center it
        Texture2D inventoryTexture = guiManager.loadGUITexture2d("container/inventory");
        Display inventory = new Display(
            guiManager,
            inventoryTexture,
            guiManager.getWindowWidth() / 2 -
                ((inventoryTexture.getImage().getWidth() * guiManager.getScale()) / 2) +
                OFFSETX * guiManager.getScale(),
            guiManager.getWindowHeight() / 2 -
                ((inventoryTexture.getImage().getHeight() * guiManager.getScale()) / 2) -
                OFFSETY * guiManager.getScale()
        );
        inventory.attachTo(inventoryNode);

        //TODO: Clean up magic Numbers (maybe define as constants)
        //DOES: Create invisible Textures on top of the item slots in the inventory so they can be replaced by textures of different items
    }

    /**
     * Changes the visibility of the Inventory. Also makes the Cursor moveable
     * @param visible Specifies the visibility to be either true or false
     */
    public void setInventoryVisibility(boolean visible) {
        if (visible) {
            inventoryNode.setCullHint(Spatial.CullHint.Inherit);
        } else {
            inventoryNode.setCullHint(Spatial.CullHint.Always);
        }
        inputManager.setCursorVisible(visible);
        flyByCamera.setEnabled(!visible); //Todo: needs to be changed
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
                slot.setTexture(guiManager.loadItemTexture2d(item.getId()));
            }
        }
    }

    public List<InventorySlot> getInventorySlots() {
        return inventorySlots;
    }
}
