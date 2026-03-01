package com.minecraftclone.gui.display;

import com.jme3.math.Vector3f;
import com.minecraftclone.gui.GUIManager;

public class InventorySlot extends Slot {

    public InventorySlot(GUIManager guiManager, int x, int y) {
        super(guiManager, x, y);
    }

    public String getText() {
        return text.getText();
    }

    public Vector3f getPosition() {
        return new Vector3f(display.getLocalTranslation());
    }
}
