package com.minecraftclone.util;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.item.ItemRegistry;

public class UIHelper {

    AssetManager assetManager;
    int scale, width, height;
    BitmapFont font;

    public UIHelper(AssetManager asset, int scale, BitmapFont font, int width, int height) {
        this.assetManager = asset;
        this.scale = scale;
        this.width = width;
        this.height = height;
        this.font = font;
    }

    public Picture createPicture(Texture2D texture, String name) {
        Picture picture = new Picture(name);
        picture.setTexture(assetManager, texture, true);
        picture.setWidth(texture.getImage().getWidth() * scale);
        picture.setHeight(texture.getImage().getHeight() * scale);
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

    @Deprecated
    public Picture createPicture(Texture2D texture, String name, int customScale) {
        Picture picture = new Picture(name);
        picture.setTexture(assetManager, texture, true);
        picture.setWidth(texture.getImage().getWidth() * customScale);
        picture.setHeight(texture.getImage().getHeight() * customScale);
        return picture;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public int getScale() {
        return scale;
    }

    public float getFontScale() {
        return scale / 4f;
    }

    public BitmapFont getFont() {
        return font;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
