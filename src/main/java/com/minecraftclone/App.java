package com.minecraftclone;

import GLOOP.*;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        var cam = new GLSchwenkkamera();
        var texture = new GLTextur("lib/bluesmurfcat.jpg");
        new GLLicht();
        new GLBoden(texture);
        new GLHimmel(texture);
        var keys = new GLTastatur();
        while (true) {
            try {
                Thread.sleep(4); // ~60 FPS
            } catch (InterruptedException e) {}
        }
    }
}
