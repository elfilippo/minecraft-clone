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

        bindKeyAction("jump", KeyInput.KEY_SPACE);
        bindKeyAction("forward", KeyInput.KEY_W);
        bindKeyAction("back", KeyInput.KEY_S);
        bindKeyAction("left", KeyInput.KEY_A);
        bindKeyAction("right", KeyInput.KEY_D);
        bindMouseAction("right-click", MouseInput.BUTTON_RIGHT);
        bindMouseAction("left-click", MouseInput.BUTTON_LEFT);

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
