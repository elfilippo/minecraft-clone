package com.minecraftclone.gui.display;

import com.jme3.texture.Texture2D;
import com.minecraftclone.util.UIHelper;

public class InventorySlot extends Slot {

    public InventorySlot(UIHelper helper, float x, float y) {
        super(helper, x, y);
    }

    public String getText() {
        return text.getText();
    }

    public Texture2D getTexture() {
        return (Texture2D) display.getMaterial().getTextureParam("Texture").getTextureValue();
    }
}
