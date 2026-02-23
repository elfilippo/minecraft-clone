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
    private Menus visibleMenu = Menus.INVENTORY;

    public MenuManager(GUIManager guiManager, InputManager inputManager, FlyByCamera flyByCamera) {
        this.guiManager = guiManager;

        inventoryGUI = new Inventory(guiManager, newNode("inventory"), inputManager, flyByCamera);
        inventorySlots = new InventorySlots(guiManager, newNode("slots"));

        setMenuVisibility(Menus.NONE);
    }

    private Node newNode(String name) {
        Node tempNode = new Node(name);
        guiManager.getGuiNode().attachChild(tempNode);
        return tempNode;
    }

    public void setMenuVisibility(Menus menu) {
        switch (menu) {
            case Menus.INVENTORY -> {
                inventoryGUI.setVisibility(true);
                inventorySlots.alignWith(inventoryGUI);
                inventorySlots.setVisibility(true);
                visibleMenu = Menus.INVENTORY;
            }
            case Menus.NONE -> {
                if (visibleMenu != Menus.NONE) {
                    setMenuInvisible(visibleMenu);
                    visibleMenu = Menus.NONE;
                }
            }
        }
    }

    private void setMenuInvisible(Menus menu) {
        switch (menu) {
            case Menus.INVENTORY -> {
                inventoryGUI.setVisibility(false);
                inventorySlots.setVisibility(false);
            }
            case Menus.NONE -> {
                break;
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
        return inventorySlots.getInventorySlot(slot);
    }

    public Menus getVisibleMenu() {
        return visibleMenu;
    }
}
