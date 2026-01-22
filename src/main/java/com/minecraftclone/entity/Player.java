package com.minecraftclone.entity;

import GLOOP.*;
import com.minecraftclone.render.Camera;

public class Player {

    Camera cam;
    GLQuader body;
    GLTastatur key;

    private double speed = 1;

    private double velX;
    private double velY;
    private double velZ;

    private double posX = 100;
    private double posY = floor; //PlayerScale/2 is floor
    private double posZ = 100;

    private static int playerScale = 128;
    private static int floor = playerScale / 2;

    public Player() {
        key = new GLTastatur();
        cam = new Camera();
        body = new GLQuader(posX, posY, posZ, playerScale / 2, playerScale, playerScale / 2);
    }

    public void movement() {
        cam.movement();
        double yaw = cam.getYaw()
        System.out.println(yaw);

        double dx = Math.sin(yaw);
        double dz = Math.cos(yaw);
    }

    private void positionUpdate(double x, double y, double z) {
        body.setzePosition(x, y, z);
        cam.setPos(x, y + 48, z); //Setzt Kamera immer auf das obere viertel des Spielers
    }
}

//tasten instanz variablen
//fly mode
//cam in y achse verschoben 96
