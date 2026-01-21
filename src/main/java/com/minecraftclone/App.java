package com.minecraftclone;

import GLOOP.*;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        var cam = new GLKamera();
        var FPSCamera = new FPSCamera(cam);
        var texture = new GLTextur("C:\\Users\\MatsOeynhausen\\OneDrive - Ruhrtal-Gymnasium Schwerte\\Dokumente\\Schule\\Schnellablage\\minecraft-clone\\src\\main\\resources\\grass.jpg");
        new GLLicht();
        new GLBoden(texture);
        new GLHimmel(texture);
        var keys = new GLTastatur();
        try {
            new CameraDebug();
        } catch (Exception e) {}
        while (true) {
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {}
        }
    }
}
