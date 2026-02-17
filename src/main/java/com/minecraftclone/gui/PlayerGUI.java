package com.minecraftclone.gui;

import com.minecraftclone.Main;
import com.minecraftclone.item.ItemInstance;
import java.io.IOException;

public class PlayerGUI {

    private int windowWidth, windowHeight;
    private int scale; //USAGE: only even numbers

    private HotbarGUI hotbarGUI;
    private InventoryGUI inventoryGUI;

    public PlayerGUI(Main main) throws IOException {
        //Does: Gets the window resolution
        this.windowWidth = main.getCamera().getWidth();
        this.windowHeight = main.getCamera().getHeight();

        //DOES: Autoscale for HUD elements based on screen resolution
        int scaleWidth = Math.round(windowWidth / 480f);
        int scaleHeight = Math.round(windowHeight / 270f);
        scale = (scaleWidth + scaleHeight) / 2;

        inventoryGUI = new InventoryGUI(main, scale);
        hotbarGUI = new HotbarGUI(main, scale);
    }

    public void inventoryDisplayItem(int row, int column, ItemInstance item) {
        inventoryGUI.displayItem(row, column, item);
        hotbarGUI.updateHotbarDisplayItem(inventoryGUI.getInventoryList(), inventoryGUI.getInventoryTextList());
    }

    public void setLife(int life) {
        hotbarGUI.setLife(life);
    }

    public void setHunger(int hunger) {
        hotbarGUI.setLife(hunger);
    }

    public void changeHotbarSelectedSlot(int slot) {
        hotbarGUI.changeHotbarSelectedSlot(slot);
    }

    public void setInventoryVisibility(boolean visible) {
        inventoryGUI.setInventoryVisibility(visible);
    }
}
