package com.minecraftclone.render;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.Camera;

public class ClampedFlyByCamera extends FlyByCamera implements AnalogListener, ActionListener {

    private float pitch = 0f; // current vertical angle in radians
    private final float minPitch = -(float) Math.PI / 2f + 0.01f;
    private final float maxPitch = (float) Math.PI / 2f - 0.01f;

    public ClampedFlyByCamera(Camera cam) {
        super(cam);
        // Disable FlyByCamera default drag cursor logic
        this.dragToRotate = false;
    }

    @Override
    public void registerWithInput(InputManager inputManager) {
        super.registerWithInput(inputManager);
        // Force cursor hidden immediately
        inputManager.setCursorVisible(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        inputManager.setCursorVisible(!enabled);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (!isEnabled()) return;

        if (name.equals("FLYCAM_Up") || name.equals("FLYCAM_Down")) {
            float dir = name.equals("FLYCAM_Up") ? -1f : 1f;
            if (invertY) dir *= -1f;

            float delta = dir * value * getRotationSpeed();
            float newPitch = pitch + delta;

            if (newPitch > maxPitch) {
                delta = maxPitch - pitch;
                pitch = maxPitch;
            } else if (newPitch < minPitch) {
                delta = minPitch - pitch;
                pitch = minPitch;
            } else {
                pitch = newPitch;
            }

            rotateCamera(delta, cam.getLeft());
            return;
        }

        // Call FlyByCamera for all other inputs
        super.onAnalog(name, value, tpf);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        // Disable drag cursor logic completely
    }
}
