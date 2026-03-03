package com.minecraftclone.gui.hud;

import com.jme3.scene.Node;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.Display;
import com.minecraftclone.gui.display.InventorySlot;
import com.minecraftclone.gui.display.Slot;
import java.util.ArrayList;
import java.util.List;

public class Hotbar {

    private GUIManager guiManager;

    private Display hotbar, selector;

    private List<Slot> hotbarSlots = new ArrayList<>();

    protected Hotbar(GUIManager guiManager, Node node) {
        this.guiManager = guiManager;

        hotbar = new Display(guiManager, guiManager.loadGUITexture2d("sprites/hud/hotbar"));
        selector = new Display(guiManager, guiManager.loadGUITexture2d("sprites/hud/hotbar_selection"));

        hotbar.setPosition(guiManager.getHalftWindowWidth() - (hotbar.getWidth() / 2), 0);

        hotbar.attachTo(node);
        selector.attachTo(node);

        for (int i = 0; i < 9; i++) {
            Slot slot = new Slot(
                guiManager,
                (guiManager.getHalftWindowWidth() - (hotbar.getWidth()) / 2) + guiManager.getScale() * (3 + 20 * i),
                3 * guiManager.getScale()
            );
            slot.attachTo(node);
            hotbarSlots.add(slot);
        }
    }

    protected void setSelectedSlot(int slot) {
        selector.setPosition(
            guiManager.getHalftWindowWidth() -
                (hotbar.getWidth() / 2 + guiManager.getScale()) +
                ((hotbar.getWidth() - 2 * guiManager.getScale()) / 9) * (slot - 1),
            0
        );
    }

    protected void updateHotbarDisplayItem(int column, InventorySlot slots) {
        Slot slot = hotbarSlots.get(column);

        slot.setTexture(slots.getTexture());
        slot.setText(slots.getText());
    }

    protected Display getHotbar() {
        return hotbar;
    }

    protected Display getSelector() {
        return selector;
    }
}
