package com.minecraftclone.gui.menu;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.minecraftclone.gui.GUIManager;

public class Inventory extends MenuGeneric {

    //TODO: Create Blockitems
    //IDEA: three textures that are transformed in a way to create an illusion of being 3d (need to be darkened to make them more 3d)
    private FlyByCamera flyByCamera;
    private InputManager inputManager;

    public Inventory(GUIManager guiManager, Node node, InputManager inputManager, FlyByCamera flyByCamera) {
        super(guiManager, node, 40, 45, "container/inventory");
        this.inputManager = inputManager;
        this.flyByCamera = flyByCamera;
    }

    @Override
    public void setVisibility(boolean visible) {
        if (visible) {
            node.setCullHint(Spatial.CullHint.Inherit);
        } else {
            node.setCullHint(Spatial.CullHint.Always);
        }
        inputManager.setCursorVisible(visible);
        flyByCamera.setEnabled(!visible); //Todo: needs to be changed
    }
}
