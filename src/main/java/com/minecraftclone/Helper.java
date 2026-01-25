package com.minecraftclone;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class Helper {

    private AppSettings settings;
    private Node guiNode;
    private AssetManager assetManager;

    public Helper(AppSettings settings, Node guiNode, AssetManager assetManager) {
        this.settings = settings;
        this.guiNode = guiNode;
        this.assetManager = assetManager;
    }

    public AppSettings getSettings() {
        return settings;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
