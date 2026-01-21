package com.minecraftclone;

import GLOOP.*;

public class App {

    static String project = System.getProperty("user.dir");

    static String textureGrass = project + "\\resources\\grass.png";
    static String textureSky = project + "\\resources\\sky.jpg";
    static String textureMarble = project + "\\resources\\marble.jpg";

    public static void main(String[] args) {
        System.out.println("Hello World!");

        Camera cam = new Camera();

        new GLLicht();
        new GLBoden(textureGrass);
        new GLHimmel(textureSky);

        while (true) {
            cam.mouseMovementListen();
        }
    }
}
