package com.minecraftclone.player.input;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class KeyMapping {

    private InputManager keys;
    private ActionListener actionListener;
    private AnalogListener analogListener;

    public KeyMapping(InputManager keys, ActionListener actionListener, AnalogListener analogListener) {
        this.keys = keys;
        this.actionListener = actionListener;
        this.analogListener = analogListener;

        bindKeyAction("FORWARD", KeyInput.KEY_W);
        bindKeyAction("BACKWARD", KeyInput.KEY_S);
        bindKeyAction("LEFT", KeyInput.KEY_A);
        bindKeyAction("RIGHT", KeyInput.KEY_D);
        bindKeyAction("JUMP", KeyInput.KEY_SPACE);

        bindKeyAction("TOGGLE_INVENTORY", KeyInput.KEY_E);
        bindKeyAction("DROP", KeyInput.KEY_Q);
        bindKeyAction("SNEAK", KeyInput.KEY_LSHIFT);
        bindKeyAction("PAUSE", KeyInput.KEY_ESCAPE);

        bindKeyAction("HOTBAR_1", KeyInput.KEY_1);
        bindKeyAction("HOTBAR_2", KeyInput.KEY_2);
        bindKeyAction("HOTBAR_3", KeyInput.KEY_3);
        bindKeyAction("HOTBAR_4", KeyInput.KEY_4);
        bindKeyAction("HOTBAR_5", KeyInput.KEY_5);
        bindKeyAction("HOTBAR_6", KeyInput.KEY_6);
        bindKeyAction("HOTBAR_7", KeyInput.KEY_7);
        bindKeyAction("HOTBAR_8", KeyInput.KEY_8);
        bindKeyAction("HOTBAR_9", KeyInput.KEY_9);

        bindMouseAction("PLACE_BLOCK", MouseInput.BUTTON_RIGHT);
        bindMouseAction("BREAK_BLOCK", MouseInput.BUTTON_LEFT);

        bindMouseAxis("MouseX+", MouseInput.AXIS_X, false);
        bindMouseAxis("MouseX-", MouseInput.AXIS_X, true);
        bindMouseAxis("MouseY+", MouseInput.AXIS_Y, false);
        bindMouseAxis("MouseY-", MouseInput.AXIS_Y, true);
    }

    private void bindKeyAction(String name, int keyCode) {
        keys.addMapping(name, new KeyTrigger(keyCode));
        keys.addListener(actionListener, name);
    }

    private void bindMouseAction(String name, int buttonCode) {
        keys.addMapping(name, new MouseButtonTrigger(buttonCode)); // Use MouseButtonTrigger for mouse events
        keys.addListener(actionListener, name);
    }

    private void bindMouseAxis(String name, int axisCode, boolean negative) {
        keys.addMapping(name, new MouseAxisTrigger(axisCode, negative));
        keys.addListener(analogListener, name);
    }
}
