package com.minecraftclone;

import GLOOP.*;
import com.minecraftclone.render.Camera;

public class App {

    static String textureGrass = "lib/textures/grass.png";
    static String textureSky = "lib/textures/sky.jpg";
    static String textureMarble = "lib/textures/marble.jpg";

    public static void main(String[] args) {
        var cam = new Camera();

        new GLLicht();
        new GLBoden(textureGrass);
        new GLHimmel(textureSky);

        while (true) {
            cam.movement();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {}
        }
    }
}
