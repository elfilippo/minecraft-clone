package com.minecraftclone.gui.display;

import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.minecraftclone.util.UIHelper;

public class Slot extends Display {

    Vector3f anchor;
    BitmapText text;

    public Slot(UIHelper helper, int x, int y) {
        super(helper, x, y);
        text = new BitmapText(helper.getFont());
        text.setLocalScale(helper.getFontScale());
        text.setLocalTranslation(x, y, 0);

        anchor = text.getLocalTranslation().clone();
        anchor.x = anchor.x + 16 * helper.getScale(); //FIXME: Magic number
        anchor.y = anchor.y + this.text.getHeight() * helper.getFontScale() - helper.getScale() - 1; //Fixme: -1?
    }

    public void setTexture(Texture2D texture) {
        display.setTexture(helper.getAssetManager(), texture, true);
    }

    public void setText(String text) {
        this.text.setText(text);
        this.text.setLocalTranslation(anchor.x - this.text.getLineWidth() * helper.getFontScale() + helper.getScale(), anchor.y, anchor.z);
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
