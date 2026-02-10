package com.minecraftclone.util;

import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

public class ImageLoader {

    private Image loadImage(String path) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(path));

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int pixel = bufferedImage.getRGB(x, y);
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
                buffer.put((byte) ((pixel >> 8) & 0xFF)); // G
                buffer.put((byte) (pixel & 0xFF)); // B
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
            }
        }

        buffer.flip();

        return new Image(Format.RGBA8, width, height, buffer, ColorSpace.sRGB);
    }

    public Texture2D loadTexture2D(String path) throws IOException {
        Texture2D texture = new Texture2D(loadImage(path));
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        texture.setMagFilter(Texture.MagFilter.Nearest);
        return texture;
    }
}
