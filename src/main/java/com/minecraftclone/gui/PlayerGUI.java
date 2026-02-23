package com.minecraftclone.gui;

import com.minecraftclone.Main;
import com.minecraftclone.gui.hud.HeadsUpDisplay;
import com.minecraftclone.gui.menu.MenuManager;
import com.minecraftclone.gui.menu.Menus;
import com.minecraftclone.item.ItemInstance;
import java.io.IOException;

public class PlayerGUI {

    private HeadsUpDisplay hud;
    private MenuManager menus;

    public PlayerGUI(Main main) throws IOException {
        //Does: Gets the window resolution
        int windowWidth = main.getCamera().getWidth();
        int windowHeight = main.getCamera().getHeight();

        GUIManager guiManager = new GUIManager(main.getAssetManager(), main.getguiFont(), main.getGuiNode(), windowWidth, windowHeight);

        //Does: Creates GUI elements
        menus = new MenuManager(guiManager, main.getInputManager(), main.getFlyByCamera());
        hud = new HeadsUpDisplay(guiManager);
    }

    /**
     * Displays an item in the Inventory
     * @param row Specifies a Row in the inventory where the Item should be displayed. 1 is the Hotbar
     * @param column Specifies a Column in the inventory where the Item should be displayed
     * @param item Specifies the item that should be displayed at the given Position
     */
    public void inventoryDisplayItem(int row, int column, ItemInstance item) {
        menus.inventoryDisplayItem(row, column, item);
        if (row == 1) {
            hud.updateHotbarDisplayItem(1, menus.getHotbarSlot(column));
        }
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
     * Changes the visibility of a menu. Also makes the Cursor moveable
     * @param visible Specifies the visibility to be either true or false
     */
    public void setMenuVisibility(Menus menu) {
        menus.setMenuVisibility(menu);
    }

    public boolean isMenuVisible() {
        if (menus.getVisibleMenu() != Menus.NONE) return true;
        else return false;
    }
}
