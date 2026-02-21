package com.minecraftclone.gui.display;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.util.UIHelper;

public class Display {

    Picture display;
    UIHelper helper;

    public Display(UIHelper helper, Texture2D texture, int x, int y) {
        this.helper = helper;

        display = helper.createPicture(texture, "display");
        display.setPosition(x, y);
    }

    public Display(UIHelper helper, Texture2D texture) {
        this.helper = helper;

        display = helper.createPicture(texture, "display");
    }

    public Display(UIHelper helper, int x, int y) {
        this.helper = helper;
        display = helper.createPicture(helper.loadItemTexture2d("golden_apple"), "display");
        display.setPosition(x, y);
    }

    public void setTexture(Texture2D texture) {
        display.setTexture(helper.getAssetManager(), texture, true);
    }

    public Texture2D getTexture() {
        return (Texture2D) display.getMaterial().getTextureParam("Texture").getTextureValue();
    }

    public void attachTo(Node node) {
        node.attachChild(display);
    }

    public void detachFrom(Node node) {
        node.detachChild(display);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            display.setCullHint(Spatial.CullHint.Inherit);
        } else {
            display.setCullHint(Spatial.CullHint.Always);
        }
    }

    public int getWidth() {
        return (int) display.getWidth();
    }

    public int getHeight() {
        return (int) display.getHeight();
    }

    public void setPosition(int x, int y) {
        display.setPosition(x, y);
    }
}
