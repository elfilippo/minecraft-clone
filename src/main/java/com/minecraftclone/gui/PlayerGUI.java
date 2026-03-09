package com.minecraftclone.gui;

import com.jme3.math.Vector2f;
import com.minecraftclone.Main;
import com.minecraftclone.gui.hud.HeadsUpDisplay;
import com.minecraftclone.gui.menu.MenuManager;
import com.minecraftclone.gui.menu.Menus;
import java.io.IOException;

public class PlayerGUI {

    private HeadsUpDisplay hud;
    private MenuManager menus;

    public PlayerGUI(Main main) throws IOException {
        //Does: Gets the window resolution
        int windowWidth = main.getCamera().getWidth();
        int windowHeight = main.getCamera().getHeight();

        GUIManager guiManager = new GUIManager(
            main.getAssetManager(),
            main.getguiFont(),
            main.getGuiNode(),
            windowWidth,
            windowHeight
        );

        //Does: Creates GUI elements
        menus = new MenuManager(guiManager, main.getInputManager(), main.getFlyByCamera()); //Fixme: menumanage should only know what it needs to (not input not cam)
        hud = new HeadsUpDisplay(guiManager);
    }

    public void inventoryDisplayItem(int index, String id, int amount) {
        menus.inventoryDisplayItem(index, id, amount);
        if (index <= 8) {
            hud.updateHotbarDisplayItem(index, menus.getHotbarSlot(index));
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

    public MenuManager getMenus() {
        return menus;
    }

    public int getClickedSlotIndex(Vector2f cursorPosition) {
        return menus.getClickedSlotIndex(cursorPosition);
    }

    public HeadsUpDisplay getHud() {
        return hud;
    }
}
