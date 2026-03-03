package com.minecraftclone.gui.menu;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.Display;

public class MenuGeneric {

    private Display menu;
    Node node;

    public MenuGeneric(GUIManager guiManager, Node node, int offsetX, int offsetY, String texturePath) {
        this.node = node;

        Texture2D texture = guiManager.loadGUITexture2d(texturePath);
        menu = new Display(
            guiManager,
            texture,
            guiManager.getWindowWidth() / 2 -
                ((texture.getImage().getWidth() * guiManager.getScale()) / 2) +
                offsetX * guiManager.getScale(),
            guiManager.getWindowHeight() / 2 -
                ((texture.getImage().getHeight() * guiManager.getScale()) / 2) -
                offsetY * guiManager.getScale()
        );
        guiManager.getGuiNode().attachChild(node);
        menu.attachTo(node);
    }

    public int getWidth() {
        return menu.getWidth();
    }

    public int getHeight() {
        return menu.getHeight();
    }

    /**
     * Changes the visibility of the Inventory. Also makes the Cursor moveable
     * @param visible Specifies the visibility to be either true or false
     */
    public void setVisibility(boolean visible) {
        if (visible) {
            node.setCullHint(Spatial.CullHint.Inherit);
        } else {
            node.setCullHint(Spatial.CullHint.Always);
        }
    }
}
