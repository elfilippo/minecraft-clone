package com.minecraftclone.gui.display;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.util.UIHelper;

public class Display {

    Picture display;
    UIHelper helper;

    protected Display(UIHelper helper, float x, float y) {
        this.helper = helper;

        display = helper.createPicture(helper.loadItemTexture2d("golden_apple"), "display");
        display.setPosition(x, y);
    }

    protected void setTexture(Texture2D texture) {
        display.setTexture(helper.getAssetManager(), texture, true);
    }

    protected void attachTo(Node node) {
        node.attachChild(display);
    }

    protected void detachFrom(Node node) {
        node.detachChild(display);
    }

    protected void setVisible(boolean visible) {
        if (visible) {
            display.setCullHint(Spatial.CullHint.Inherit);
        } else {
            display.setCullHint(Spatial.CullHint.Always);
        }
    }
}
