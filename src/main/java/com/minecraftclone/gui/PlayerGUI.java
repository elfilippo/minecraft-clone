package com.minecraftclone.gui;

import com.minecraftclone.Main;
import com.minecraftclone.gui.hud.HeadsUpDisplay;
import com.minecraftclone.gui.menu.Inventory;
import com.minecraftclone.item.ItemInstance;
import java.io.IOException;

public class PlayerGUI {

    private HeadsUpDisplay hud;
    private Inventory inventoryGUI;

    public PlayerGUI(Main main) throws IOException {
        //Does: Gets the window resolution
        int windowWidth = main.getCamera().getWidth();
        int windowHeight = main.getCamera().getHeight();

        GUIManager guiManager = new GUIManager(main.getAssetManager(), main.getguiFont(), main.getGuiNode(), windowWidth, windowHeight);

        //Does: Creates GUI elements
        inventoryGUI = new Inventory(main, guiManager);
        hud = new HeadsUpDisplay(guiManager);
    }

    /**
     * Displays an item in the Inventory
     * @param row Specifies a Row in the inventory where the Item should be displayed. 1 is the Hotbar
     * @param column Specifies a Column in the inventory where the Item should be displayed
     * @param item Specifies the item that should be displayed at the given Position
     */
    public void inventoryDisplayItem(int row, int column, ItemInstance item) {
        inventoryGUI.displayItem(row, column, item);
        hud.updateHotbarDisplayItem(inventoryGUI.getInventorySlots());
    }

    /**
     * Sets the number of hearts that should be displayed in the HUD
     * @param life Specifies the number of hearts that should be displayed in the HUD
     */
    public void setLife(int life) {
        hud.setLife(life);
    }

    /**
     * Sets the number of hunger bars that should be displayed in the HUD
     * @param hunger Specifies the number of hunger bars that should be displayed in the HUD
     */
    public void setHunger(int hunger) {
        hud.setHunger(hunger);
    }

    /**
     * Changes the Position of the Hotbar-Selector
     * @param slot Slot where the Hotbar-Selector is displayed
     */
    public void setHotbarSelectedSlot(int slot) {
        hud.setHotbarSelectedSlot(slot);
    }

    /**
     * Changes the visibility of the Inventory. Also makes the Cursor moveable
     * @param visible Specifies the visibility to be either true or false
     */
    public void setInventoryVisibility(boolean visible) {
        inventoryGUI.setInventoryVisibility(visible);
    }
}
