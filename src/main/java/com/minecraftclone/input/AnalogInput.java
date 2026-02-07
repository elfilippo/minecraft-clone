package com.minecraftclone.input;

import com.jme3.input.controls.AnalogListener;

public class AnalogInput implements AnalogListener {

    float mouseWheelUp, mouseWheelDown;

    @Override
    public void onAnalog(String name, float analogPressed, float tpf) {
        switch (name) {
            case "mouseWheelUp" -> mouseWheelUp = analogPressed;
            case "mouseWheelDown" -> mouseWheelDown = analogPressed;
        }
    }
}
