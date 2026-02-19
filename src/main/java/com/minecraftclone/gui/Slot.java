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
    UIHelper helper;

    Slot(UIHelper helper, float x, float y) {
        this.helper = helper;

        display = helper.createPicture(helper.loadItemTexture2d("golden_apple"), "display");
        display.setPosition(x, y);

        text = new BitmapText(helper.getFont());
        text.setLocalScale(helper.getFontScale());
        text.setLocalTranslation(x, y, 0);

        anchor = text.getLocalTranslation().clone();
        anchor.x = anchor.x + 16 * helper.getScale(); //FIXME: Magic number
        anchor.y = anchor.y + this.text.getHeight() * helper.getFontScale();
    }

    void setTexture(AssetManager asset, Texture2D texture) {
        display.setTexture(asset, texture, true);
    }

    void setText(String text) {
        this.text.setText(text);
        this.text.setLocalTranslation(anchor.x - this.text.getLineWidth() * helper.getFontScale(), anchor.y, anchor.z);
    }

    void attach(Node node) {
        //FIXME: Kinda ass
        node.attachChild(display);
        node.attachChild(text);
    }

    void detach(Node node) {
        //FIXME: not detaching but cullhint
        node.detachChild(display);
        node.detachChild(text);
    }
}
