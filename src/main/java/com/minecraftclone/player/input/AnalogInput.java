package com.minecraftclone.player.input;

import com.jme3.input.controls.AnalogListener;

public class AnalogInput implements AnalogListener {

    float mouseWheelUp, mouseWheelDown;

    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch (name) {
            case "INVENTORY_SLOT_UP" -> mouseWheelUp = value;
            case "INVENTORY_SLOT_DOWN" -> mouseWheelDown = value;
        }
    }

    public float getMouseWheelDown() {
        float temp = mouseWheelDown;
        mouseWheelDown = 0f;
        return temp;
    }

    public float getMouseWheelUp() {
        float temp = mouseWheelUp;
        mouseWheelUp = 0f;
        return temp;
    }
}
