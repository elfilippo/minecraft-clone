package com.minecraftclone.util;

import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {

    private static final Map<String, Texture2D> ITEM_CACHE = new HashMap<>();
    private static final Map<String, Texture2D> GUI_CACHE = new HashMap<>();

    AssetManager assetManager;
    int scale;

    public TextureManager(AssetManager asset, int scale) {
        this.assetManager = asset;
        this.scale = scale;
    }

    public static Texture2D getItemTexture(String path) {
        if (ITEM_CACHE.containsKey(path)) {
            return ITEM_CACHE.get(path);
        }

        String fullPath = "textures/item/" + path + ".png";

        try (InputStream stream = TextureManager.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (stream == null) {
                throw new RuntimeException("Texture not found: " + fullPath);
            }

            Texture2D texture = ImageLoader.loadTexture2D(stream);

            ITEM_CACHE.put(path, texture);
            return texture;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture: " + fullPath, e);
        }
    }

    public static Texture2D getGuiTexture(String path) {
        if (GUI_CACHE.containsKey(path)) {
            return GUI_CACHE.get(path);
        }

        String fullPath = "textures/gui/" + path + ".png";

        try (InputStream stream = TextureManager.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (stream == null) {
                throw new RuntimeException("Texture not found: " + fullPath);
            }

            Texture2D texture = ImageLoader.loadTexture2D(stream);

            GUI_CACHE.put(path, texture);
            return texture;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture: " + fullPath, e);
        }
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
        picture.setWidth(texture.getImage().getWidth() * customScale);
        picture.setHeight(texture.getImage().getHeight() * customScale);
        return picture;
    }
}
