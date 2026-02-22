package com.minecraftclone.gui.menu;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.InventorySlot;
import com.minecraftclone.item.ItemInstance;

public class MenuManager {

    private Inventory inventoryGUI;
    private InventorySlots inventorySlots;
    private GUIManager guiManager;

    public MenuManager(GUIManager guiManager, InputManager inputManager, FlyByCamera flyByCamera) {
        this.guiManager = guiManager;

        inventoryGUI = new Inventory(guiManager, newNode("inventory"), inputManager, flyByCamera);
        inventorySlots = new InventorySlots(guiManager, newNode("slots"));
    }

    private Node newNode(String name) {
        Node tempNode = new Node(name);
        guiManager.getGuiNode().attachChild(tempNode);
        return tempNode;
    }

    public void setMenuVisibility(Menus menu, boolean visible) {
        switch (menu) {
            case Menus.INVENTORY -> {
                inventoryGUI.setVisibility(visible);
                if (visible) {
                    inventorySlots.alignWith(inventoryGUI);
                }
                inventorySlots.setVisibility(visible);
            }
        }
    }

    public InventorySlots getInventorySlots() {
        return inventorySlots;
    }

    public void inventoryDisplayItem(int row, int column, ItemInstance item) {
        inventorySlots.displayItem(row, column, item);
    }

    public InventorySlot getHotbarSlot(int slot) {
        return inventorySlots.getInventorySlots(slot);
    }
}
