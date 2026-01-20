package com.minecraftclone;

import GLOOP.*;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        var cam = new GLKamera();
        var texture = new GLTextur("C:/Users/theel/Desktop/Meine Sachen/Bilder/Privat/aihoshino.png");
        new GLLicht();
        new GLBoden(texture);
        new GLHimmel(texture);
        var keys = new GLTastatur();
    }
}
