package com.minecraftclone.gui;

import com.minecraftclone.Main;
import com.minecraftclone.item.ItemInstance;
import java.io.IOException;

//Todo: Make text scalable and iprove scaling
public class PlayerGUI {

    private int windowWidth, windowHeight;
    private int scale; //USAGE: only even numbers

    private HUD hud;
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
        hud = new HUD(main, scale);
    }

    public void inventoryDisplayItem(int row, int column, ItemInstance item) {
        inventoryGUI.displayItem(row, column, item);
        hud.updateHotbarDisplayItem(inventoryGUI.getInventoryList(), inventoryGUI.getInventoryTextList());
    }

    public void setLife(int life) {
        hud.setLife(life);
    }

    public void setHunger(int hunger) {
        hud.setLife(hunger);
    }

    public void changeHotbarSelectedSlot(int slot) {
        hud.changeHotbarSelectedSlot(slot);
    }

    public void setInventoryVisibility(boolean visible) {
        inventoryGUI.setInventoryVisibility(visible);
    }
}
