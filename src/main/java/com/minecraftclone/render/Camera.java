package com.minecraftclone.render;

import GLOOP.*;
import java.awt.*;
import javax.swing.JFrame;

public class Camera {

    private Point location;
    private Robot robot; //TODO: automatically center curosor with robot and hide it
    private JFrame frame;
    private GLKamera cam;
    private GLMaus mouse;

    private double yaw;
    private double pitch;

    private int lastX;
    private int lastY;

    private int windowCenterX;
    private int windowCenterY;

    private double sensitivity = 0.01;

    private final double maxPitch = Math.toRadians(89);

    public Camera() {
        cam = new GLKamera(1280, 720);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        frame = cam.gibFrame();
        location = frame.getLocationOnScreen();

        windowCenterX = location.x + frame.getWidth() / 2;
        windowCenterY = location.y + frame.getHeight() / 2;

        mouse = new GLMaus();

        lastX = mouse.gibX();
        lastY = mouse.gibY();
        cam.setzePosition(0, 100, 0);
    }

    public void movement() {
        if (mouse.gibX() != lastX || mouse.gibY() != lastY) {
            int dx = mouse.gibX() - lastX;
            int dy = mouse.gibY() - lastY;

            yaw -= dx * sensitivity;
            pitch -= dy * sensitivity;

            pitch = Math.max(-maxPitch, Math.min(maxPitch, pitch)); //Clamp

            update();

            lastX = mouse.gibX();
            lastY = mouse.gibY();
        }
        //System.out.println(cam.gibBlickpunktX() + " - " + cam.gibBlickpunktY() + " - " + cam.gibBlickpunktZ());
    }

    private void update() {
        GLVektor pos = cam.gibPosition();

        double dirX = Math.cos(pitch) * Math.sin(yaw);
        double dirY = Math.sin(pitch);
        double dirZ = Math.cos(pitch) * Math.cos(yaw);

        cam.setzeBlickpunkt(pos.x + dirX, pos.y + dirY, pos.z + dirZ);
        cam.setzeScheitelrichtung(0, 1, 0);
    }

    public double getYaw() {
        return yaw;
    }

    public void setPos(double x, double y, double z) {
        cam.setzePosition(x, y, z);
    }
}
