package com.minecraftclone.render;

import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;

public class ClampedFlyByCamera extends FlyByCamera {

    private float pitch = 0f; // current vertical angle in radians
    private final float minPitch = -(float) Math.PI / 2f + 0.01f;
    private final float maxPitch = (float) Math.PI / 2f - 0.01f;

    public ClampedFlyByCamera(Camera cam) {
        super(cam);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (!isEnabled()) {
            return;
        }

        if (name.equals("FLYCAM_Up") || name.equals("FLYCAM_Down")) {
            float dir = name.equals("FLYCAM_Up") ? -1f : 1f;
            if (invertY) {
                dir *= -1f;
            }

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

            if (delta != 0) {
                rotateCamera(delta, cam.getLeft());
            }
            return;
        }

        // For all other inputs, use the default FlyByCamera behavior
        super.onAnalog(name, value, tpf);
    }
}