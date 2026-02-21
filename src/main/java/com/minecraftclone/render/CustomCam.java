package com.minecraftclone.render;

import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;

public class CustomCam extends FlyByCamera {

    //IS: current vertical angle in radians
    private float pitch = 0f;
    private final float minPitch = -(float) Math.PI / 2f + 0.01f;
    private final float maxPitch = (float) Math.PI / 2f - 0.01f;

    public CustomCam(Camera cam) {
        //DOES: create FlyByCamera (parent class) with overrides in this class
        super(cam);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        //DOES: override input taking to clamp pitch

        if (!isEnabled()) {
            return;
        }

        if (name.equals("FLYCAM_Up") || name.equals("FLYCAM_Down")) {
            //DOES: set dir to -1f if true, 1f if false (ternary operator)
            float dir = name.equals("FLYCAM_Up") ? -1f : 1f;

            if (invertY) {
                dir *= -1f;
            }

            //IS: delta of old and new location
            float delta = dir * value * getRotationSpeed();

            //IS: proposed new pitch
            float newPitch = pitch + delta;

            //DOES: check if the new pitch would exceed bounds and clamps if so
            if (newPitch > maxPitch) {
                delta = maxPitch - pitch;
                pitch = maxPitch;
            } else if (newPitch < minPitch) {
                delta = minPitch - pitch;
                pitch = minPitch;
            } else {
                pitch = newPitch;
            }

            //DOES: rotate cam if needs to be rotated
            if (delta != 0) {
                rotateCamera(delta, cam.getLeft());
            }
            return;
        }

        //DOES: use the default FlyByCamera behavior for all other inputs
        super.onAnalog(name, value, tpf);
    }
}
