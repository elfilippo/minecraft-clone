package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.item.ItemRegistry;

public class GUIManager {

    private AssetManager assetManager;
    private int scale, windowWidth, windowHeight, halftWindowWidth, halftWindowHeight;
    private float fontScale;
    private BitmapFont font;
    private Node guiNode;

    public GUIManager(AssetManager asset, BitmapFont font, Node guiNode, int windowWidth, int windowHeight) {
        this.assetManager = asset;
        this.font = font;
        this.guiNode = guiNode;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.halftWindowWidth = windowWidth / 2;
        this.halftWindowHeight = windowHeight / 2;

        //Does: Autoscaling
        int scale = Math.min(windowWidth / 480, windowHeight / 270);
        this.scale = Math.max(1, scale);
        this.fontScale = this.scale / 4f;
    }

    public Picture createPicture(Texture2D texture, String name) {
        Picture picture = new Picture(name);
        picture.setTexture(assetManager, texture, true);
        picture.setWidth(texture.getImage().getWidth() * scale);
        picture.setHeight(texture.getImage().getHeight() * scale);
        return picture;
    }

    public Picture createPicture(Texture2D texture, String name, int customScale) {
        Picture picture = new Picture(name);
        picture.setTexture(assetManager, texture, true);
        picture.setWidth(texture.getImage().getWidth() * scale * customScale);
        picture.setHeight(texture.getImage().getHeight() * scale * customScale);
        return picture;
    }

    public Texture2D loadItemTexture2d(String id) {
        Texture2D texture = (Texture2D) assetManager.loadTexture(ItemRegistry.getTexturePath(id));
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        texture.setMagFilter(Texture.MagFilter.Nearest);
        return texture;
    }

    public Texture2D loadGUITexture2d(String name) {
        Texture2D texture = (Texture2D) assetManager.loadTexture("textures/gui/" + name + ".png");
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        texture.setMagFilter(Texture.MagFilter.Nearest);
        return texture;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public int getScale() {
        return scale;
    }

    public float getFontScale() {
        return fontScale;
    }

    public BitmapFont getFont() {
        return font;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public int getHalftWindowWidth() {
        return halftWindowWidth;
    }

    public int getHalftWindowHeight() {
        return halftWindowHeight;
    }
}
