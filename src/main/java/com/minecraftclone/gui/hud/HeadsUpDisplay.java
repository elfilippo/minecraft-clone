package com.minecraftclone.gui.hud;

import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.InventorySlot;
import java.util.List;

public class HeadsUpDisplay {

    private Picture experienceBarEmpty, crosshair;
    private Node hudNode;

    private int scale, halfWidth, halfHeight;

    private HeartsDisplay heartsDisplay;
    private HungerDisplay hungerDisplay;
    private Hotbar hotbar;

    private int selectedSlot;

    public HeadsUpDisplay(GUIManager guiManager) {
        Node guiNode = guiManager.getGuiNode();

        //DOES: Create Nodes for layering and attaches them
        hudNode = new Node("hudNode");
        guiNode.attachChild(hudNode);

        //DOES: Creates different parts of the gui
        hotbar = new Hotbar(guiManager, hudNode);

        crosshair = guiManager.createPicture(guiManager.loadGUITexture2d("sprites/hud/crosshair"), "crosshair");
        experienceBarEmpty = guiManager.createPicture(
            guiManager.loadGUITexture2d("sprites/hud/experience_bar_background"),
            "experienceBarEmpty"
        );

        heartsDisplay = new HeartsDisplay(
            guiManager,
            (int) (halfWidth - (hotbar.getHotbar().getWidth() / 2)),
            (int) (experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHotbar().getHeight()),
            hudNode
        );
        hungerDisplay = new HungerDisplay(
            guiManager,
            (int) (halfWidth + hotbar.getHotbar().getWidth() / 2 - 9 * scale),
            (int) (experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHotbar().getHeight()),
            hudNode
        );

        //DOES: Sets the Position of some gui elements on the screen
        experienceBarEmpty.setPosition(halfWidth - ((experienceBarEmpty.getWidth() / 2)), hotbar.getHotbar().getHeight() + scale * 2);
        crosshair.setPosition(halfWidth - ((crosshair.getWidth() / 2)), halfHeight - ((crosshair.getHeight() / 2)));

        //DOES: Attaches gui elements to Nodes
        hudNode.attachChild(experienceBarEmpty);
        hudNode.attachChild(crosshair);
    }

    /**
     * Changes the heart textures in order to display the players life. odd numbers make half hearts
     * @param life hearts that should be displayed in the HUD
     */
    public void setLife(int life) {
        heartsDisplay.setLife(life);
    }

    /**
     * Changes the hunger bars textures in order to display the players hunger. odd numbers make half hearts
     * @param huger hunger bars that should be displayed in the HUD
     */
    public void setHunger(int hunger) {
        hungerDisplay.setHunger(hunger);
    }

    /**
     * Updates the Hotbar items with the items displayed in the inventory
     * @param invPic List of all Item Pictures in the inventory
     * @param invText List of all Item Texts in the inventory
     */
    public void updateHotbarDisplayItem(List<InventorySlot> slots) {
        hotbar.updateHotbarDisplayItems(slots);
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    /**
     * Changes the displayed selected slot
     * @param slot Specifies the slot that should appear selected
     */
    public void setHotbarSelectedSlot(int slot) {
        if (slot <= 9 && slot >= 1) {
            selectedSlot = slot;
            hotbar.setSelectedSlot(slot);
        }
    }
}
