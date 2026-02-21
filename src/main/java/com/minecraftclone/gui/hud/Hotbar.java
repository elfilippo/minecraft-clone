package com.minecraftclone.gui.hud;

import com.jme3.scene.Node;
import com.minecraftclone.gui.display.Display;
import com.minecraftclone.util.UIHelper;

public class Hotbar {

    private Display hotbar, hotbarSelector;

    protected Hotbar(UIHelper helper, Node node) {
        hotbar = new Display(helper, helper.loadGUITexture2d("sprites/hud/hotbar"));
        hotbarSelector = new Display(helper, helper.loadGUITexture2d("sprites/hud/hotbar_selection"));

        hotbar.attachTo(node);
        hotbarSelector.attachTo(node);
    }

    public Display getHotbar() {
        return hotbar;
    }

    public Display getHotbarSelector() {
        return hotbarSelector;
    }

    public void setSelectorPosition(int x, int y) {
        hotbarSelector.setPosition(x, y);
    }
}
