package com.minecraftclone.gui.menu;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.Slot;

public class MenuManager {

    private Inventory inventoryGUI;
    private InventorySlots inventorySlots;
    private GUIManager guiManager;
    private Menus visibleMenu = Menus.INVENTORY;

    public MenuManager(GUIManager guiManager, InputManager inputManager, FlyByCamera flyByCamera) {
        this.guiManager = guiManager;

        inventoryGUI = new Inventory(guiManager, newNode("inventory"), inputManager, flyByCamera);
        inventorySlots = new InventorySlots(guiManager, newNode("fuckingSlots"));

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

    public void inventoryDisplayItem(int index, String id, int amount) {
        inventorySlots.displayItem(index, id, amount);
    }

    public Slot getHotbarSlot(int slot) {
        return inventorySlots.getSlot(slot);
    }

    public Menus getVisibleMenu() {
        return visibleMenu;
    }

    public int getClickedSlotIndex(Vector2f cursorPosition) {
        return inventorySlots.getClickedSlotIndex(cursorPosition);
    }
}
