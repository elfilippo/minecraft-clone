package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.util.UIHelper;

class Slot {

    Vector3f anchor;
    BitmapText text;
    Picture display;

    Slot(UIHelper helper, float x, float y) {
        display = helper.createPicture(helper.loadItemTexture2d("golden_apple"), "display");
        display.setPosition(x, y);

        text = new BitmapText(helper.getFont());
        text.setLocalScale(helper.getFontScale());
        text.setLocalTranslation(x, y, 0);

        anchor = text.getLocalTranslation().clone();
    }

    void setTexture(AssetManager asset, Texture2D texture) {
        display.setTexture(asset, texture, true);
    }

    void setText(String text) {
        this.text.setText(text);
    }

    void attach(Node node) {
        node.attachChild(display);
        node.attachChild(text);
    }

    void detach(Node node) {
        node.detachChild(display);
        node.detachChild(text);
    }
}
