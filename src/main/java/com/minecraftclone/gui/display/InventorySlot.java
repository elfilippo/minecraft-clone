package com.minecraftclone.gui.display;

import com.minecraftclone.gui.GUIManager;

public class InventorySlot extends Slot {

    public InventorySlot(GUIManager guiManager, int x, int y) {
        super(guiManager, x, y);
    }

    public String getText() {
        return text.getText();
    }
}
