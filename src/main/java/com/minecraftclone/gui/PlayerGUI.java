package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.item.ItemInstance;
import com.minecraftclone.util.ImageLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerGUI {

    private int selectedSlot = 1;
    private int scale = 3;
    private Picture hotbar;
    private Picture hotbarSelector;
    private int windowWidth;

    List<ItemInstance> slots = new ArrayList<>(9);

    public PlayerGUI(AppSettings settings, Node guiNode, AssetManager assetManager) throws IOException {
        windowWidth = settings.getWidth();
        ImageLoader imageLoader = new ImageLoader();

        Texture2D hotbarTexture = new Texture2D(imageLoader.loadImage("src/main/resources/textures/gui/sprites/hud/hotbar.png")); //182x22
        hotbarTexture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        hotbarTexture.setMagFilter(Texture.MagFilter.Nearest);

        Texture2D hotbarSelectorTexture = new Texture2D(
            imageLoader.loadImage("src/main/resources/textures/gui/sprites/hud/hotbar_selection.png") //24x23
        );
        hotbarSelectorTexture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        hotbarSelectorTexture.setMagFilter(Texture.MagFilter.Nearest);

        hotbar = new Picture("hotbar");
        hotbar.setTexture(assetManager, hotbarTexture, true);
        hotbar.setWidth(182 * scale);
        hotbar.setHeight(22 * scale);
        hotbar.setPosition(windowWidth / 2 - (hotbar.getWidth() / 2), 0);
        guiNode.attachChild(hotbar);

        hotbarSelector = new Picture("hotbarSelector");
        hotbarSelector.setTexture(assetManager, hotbarSelectorTexture, true);
        hotbarSelector.setWidth(24 * scale);
        hotbarSelector.setHeight(23 * scale);
        hotbarSelector.setPosition(windowWidth / 2 - ((hotbar.getWidth() / 2) + 1 * scale), 0);
        guiNode.attachChild(hotbarSelector);

        changeHotbarSlot(selectedSlot);
    }

    public void changeHotbarSlot(int slot) {
        //nicht hinterfragen
        if (slot <= 9 && slot >= 1) {
            selectedSlot = slot;
            hotbarSelector.setPosition(
                windowWidth / 2 -
                    ((hotbar.getWidth() / 2) + 1 * scale) -
                    (hotbar.getWidth() - 2 * scale) / 9 +
                    (((hotbar.getWidth() - 2 * scale) / 9) * slot),
                0
            );
        }
    }

    public void hotbarSlotUp() {
        changeHotbarSlot(selectedSlot + 1);
    }

    public void hotbarSlotDown() {
        changeHotbarSlot(selectedSlot - 1);
    }
}
