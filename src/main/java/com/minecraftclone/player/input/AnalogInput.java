package com.minecraftclone.player.input;

import com.jme3.input.controls.AnalogListener;

public class AnalogInput implements AnalogListener {

    private float mouseDX;
    private float mouseDY;

    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch (name) {
            case "MouseX+" -> mouseDX = value;
            case "MouseX-" -> mouseDX = -value;
            case "MouseY+" -> mouseDY = value;
            case "MouseY-" -> mouseDY = -value;
        }
        //System.out.println(value);
    }

    public float consumeMouseDX() {
        float dx = mouseDX;
        mouseDX = 0;
        return dx;
    }

    public float consumeMouseDY() {
        float dy = mouseDY;
        mouseDY = 0;
        return dy;
    }
}
