package com.minecraftclone.entitiy;

import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.Helper;
import com.minecraftclone.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private int selectedSlot = 9;
    private int scale = 3;
    private Picture inventory;
    private Picture inventorySelector;
    private int windowWidth;

    List<ItemStack> slots = new ArrayList<>(9);

    Inventory(Helper helper) {
        windowWidth = helper.getSettings().getWidth();

        inventory = new Picture("inventory");
        Texture2D inventoryTexture = new Texture2D();
        inventory.setTexture(helper.getAssetManager(), inventoryTexture, true);
        inventory.setWidth(182 * scale);
        inventory.setHeight(22 * scale);
        inventory.setPosition(windowWidth / 2 - (inventory.getWidth() / 2), 0);
        helper.getGuiNode().attachChild(inventory);

        inventorySelector = new Picture("inventorySelector");
        inventorySelector.setImage(helper.getAssetManager(), "textures/gui/sprites/hud/hotbar_selection.png", true);
        inventorySelector.setWidth(24 * scale);
        inventorySelector.setHeight(23 * scale);
        inventorySelector.setPosition(windowWidth / 2 - ((inventory.getWidth() / 2) + 1 * scale), 0);
        helper.getGuiNode().attachChild(inventorySelector);

        changeSlot(selectedSlot);
    }

    public void changeSlot(int slot) {
        //nicht hinterfragen
        selectedSlot = slot;
        inventorySelector.setPosition(
            windowWidth / 2 -
                ((inventory.getWidth() / 2) + 1 * scale) -
                (inventory.getWidth() - 2 * scale) / 9 +
                (((inventory.getWidth() - 2 * scale) / 9) * slot),
            0
        );
    }
}
