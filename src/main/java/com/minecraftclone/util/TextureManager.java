package com.minecraftclone.util;

import com.jme3.texture.Texture2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {

    private static final Map<String, Texture2D> CACHE = new HashMap<>();

    public static Texture2D getItemTexture(String path) {
        if (CACHE.containsKey(path)) {
            return CACHE.get(path);
        }

        String fullPath = "textures/item" + path + ".png";

        try (InputStream stream = TextureManager.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (stream == null) {
                throw new RuntimeException("Texture not found: " + fullPath);
            }

            Texture2D texture = ImageLoader.loadTexture2D(stream);

            CACHE.put(path, texture);
            return texture;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture: " + fullPath, e);
        }
    }

    public static Texture2D getGuiTexture(String path) {
        if (CACHE.containsKey(path)) {
            return CACHE.get(path);
        }

        String fullPath = "textures/gui" + path + ".png";

        try (InputStream stream = TextureManager.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (stream == null) {
                throw new RuntimeException("Texture not found: " + fullPath);
            }

            Texture2D texture = ImageLoader.loadTexture2D(stream);

            CACHE.put(path, texture);
            return texture;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture: " + fullPath, e);
        }
    }
}
