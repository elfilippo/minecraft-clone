package com.minecraftclone.gui.display;

import com.minecraftclone.util.UIHelper;

public class InventorySlot extends Slot {

    public InventorySlot(UIHelper helper, int x, int y) {
        super(helper, x, y);
    }

    public String getText() {
        return text.getText();
    }
}
