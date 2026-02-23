package com.minecraftclone.gui.display;

import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.minecraftclone.gui.GUIManager;

public class Slot extends Display {

    private static final int SLOT_WIDTH = 16;

    Vector3f anchor;
    BitmapText text;

    public Slot(GUIManager guiManager, int x, int y) {
        super(guiManager, x, y);
        text = new BitmapText(guiManager.getFont());
        text.setLocalScale(guiManager.getFontScale());
        text.setLocalTranslation(x, y, 0);

        anchor = text.getLocalTranslation().clone();
        anchor.x = anchor.x + SLOT_WIDTH * guiManager.getScale();
        anchor.y = anchor.y + this.text.getHeight() * guiManager.getFontScale() - guiManager.getScale() - 1;
    }

    public void setText(String text) {
        this.text.setText(text);
        this.text.setLocalTranslation(
            anchor.x - this.text.getLineWidth() * guiManager.getFontScale() + guiManager.getScale(),
            anchor.y,
            anchor.z
        );
    }

    @Override
    public void attachTo(Node node) {
        node.attachChild(display);
        node.attachChild(text);
    }

    @Override
    public void detachFrom(Node node) {
        node.detachChild(display);
        node.detachChild(text);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            display.setCullHint(Spatial.CullHint.Inherit);
            text.setCullHint(Spatial.CullHint.Inherit);
        } else {
            display.setCullHint(Spatial.CullHint.Always);
            text.setCullHint(Spatial.CullHint.Always);
        }
    }
}
