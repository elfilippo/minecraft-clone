package com.minecraftclone.gui.display;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.gui.GUIManager;

public class Display {

    Picture display;
    GUIManager guiManager;

    public Display(GUIManager guiManager, Texture2D texture, int x, int y) {
        this.guiManager = guiManager;

        display = guiManager.createPicture(texture, "display");
        display.setPosition(x, y);
    }

    public Display(GUIManager guiManager, Texture2D texture) {
        this.guiManager = guiManager;

        display = guiManager.createPicture(texture, "display");
    }

    public Display(GUIManager guiManager, int x, int y) {
        this.guiManager = guiManager;
        display = guiManager.createPicture(guiManager.loadGUITexture2d("blank"), "display", 16);
        display.setPosition(x, y);
    }

    public void setTexture(Texture2D texture) {
        display.setTexture(guiManager.getAssetManager(), texture, true);
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
