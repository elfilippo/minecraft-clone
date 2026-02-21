package com.minecraftclone.gui.hud;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.Main;
import com.minecraftclone.gui.display.InventorySlot;
import com.minecraftclone.gui.display.Slot;
import com.minecraftclone.util.UIHelper;
import java.util.ArrayList;
import java.util.List;

public class HeadsUpDisplay {

    private AssetManager asset;
    private Picture experienceBarEmpty, crosshair;
    private Node hudNode, statsNode;

    private int scale, halfWidth, halfHeight;

    private HeartsDisplay heartsDisplay;
    private HungerDisplay hungerDisplay;
    private Hotbar hotbar;

    private List<Slot> hotbarSlots = new ArrayList<>();
    private int selectedSlot;

    public HeadsUpDisplay(Main main, int scale) {
        Node guiNode = main.getGuiNode();
        BitmapFont font = main.getguiFont();
        this.asset = main.getAssetManager();
        this.scale = scale;

        halfWidth = main.getCamera().getWidth() / 2;
        System.out.println(halfWidth);
        halfHeight = main.getCamera().getHeight() / 2;

        UIHelper uiHelper = new UIHelper(asset, scale, font, main.getCamera().getWidth(), main.getCamera().getHeight());

        //DOES: Create Nodes for layering and attach them
        hudNode = new Node("hudNode");
        statsNode = new Node("statsNode");

        guiNode.attachChild(hudNode);
        hudNode.attachChild(statsNode);

        //DOES: Create Texture variables
        Texture2D crosshairTexture = uiHelper.loadGUITexture2d("sprites/hud/crosshair"); //15x15
        Texture2D experienceBarEmptyTexture = uiHelper.loadGUITexture2d("sprites/hud/experience_bar_background"); //182x5

        hotbar = new Hotbar(uiHelper, hudNode);
        hotbar.getHotbar().setPosition(halfWidth - (hotbar.getHotbar().getWidth() / 2), 0);

        //DOES: Create Pictures to display in the GUI, positions them and attaches them to nodes
        experienceBarEmpty = uiHelper.createPicture(experienceBarEmptyTexture, "experienceBarEmpty");
        crosshair = uiHelper.createPicture(crosshairTexture, "crosshair");

        experienceBarEmpty.setPosition(halfWidth - ((experienceBarEmpty.getWidth() / 2)), hotbar.getHotbar().getHeight() + scale * 2);
        crosshair.setPosition(halfWidth - ((crosshair.getWidth() / 2)), halfHeight - ((crosshair.getHeight() / 2)));

        hudNode.attachChild(experienceBarEmpty);
        hudNode.attachChild(crosshair);

        heartsDisplay = new HeartsDisplay(
            uiHelper,
            (int) (halfWidth - (hotbar.getHotbar().getWidth() / 2)),
            (int) (experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHotbar().getHeight()),
            statsNode
        );
        hungerDisplay = new HungerDisplay(
            uiHelper,
            (int) (halfWidth + hotbar.getHotbar().getWidth() / 2 - 9 * scale),
            (int) (experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHotbar().getHeight()),
            statsNode
        );

        //DOES: Creates empty textures and text on top of the Hotbar to display items placed there
        for (int i = 0; i < 9; i++) {
            Slot slot = new Slot(uiHelper, (halfWidth - (hotbar.getHotbar().getWidth()) / 2) + scale * (3 + 20 * i), 3 * scale);
            slot.attachTo(hudNode);
            hotbarSlots.add(slot);
        }
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
        //Info: The items displayed in the Hotbar are copied from those in the inventoryList, because the inventory also has a Hotbar
        //Does: Checks for differences between the inventory hotbar and real hotbar and if they are not the same displays the item in the inventory hotbar in the hotbar
        for (int i = 0; i < 9; i++) {
            Slot slot = hotbarSlots.get(i);

            slot.setTexture(slots.get(i).getTexture());
            slot.setText(slots.get(i).getText());
        }
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
            hotbar.setSelectorPosition(
                halfWidth - (hotbar.getHotbar().getWidth() / 2 + scale) + ((hotbar.getHotbar().getWidth() - 2 * scale) / 9) * (slot - 1),
                0
            );
        }
    }
}
