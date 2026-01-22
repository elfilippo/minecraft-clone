package com.minecraftclone;

import GLOOP.*;
import com.minecraftclone.entity.Player;

public class App {

    static String textureGrass = "lib/textures/grass.png";
    static String textureSky = "lib/textures/sky.jpg";
    static String textureMarble = "lib/textures/marble.jpg";

    public static void main(String[] args) {
        Player player = new Player(); //Player creates cam

        new GLLicht();
        new GLBoden(textureGrass);
        new GLHimmel(textureSky);

        while (true) {
            player.movement();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {}
        }
    }
}
