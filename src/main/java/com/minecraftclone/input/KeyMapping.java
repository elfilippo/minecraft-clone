package com.minecraftclone.input;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class KeyMapping {

    private InputManager keys;
    private ActionListener actionListener;

    public KeyMapping(InputManager keys, ActionListener actionListener) {
        this.keys = keys;
        this.actionListener = actionListener;

        bindKeyAction("jump", KeyInput.KEY_SPACE);
        bindKeyAction("forward", KeyInput.KEY_W);
        bindKeyAction("back", KeyInput.KEY_S);
        bindKeyAction("left", KeyInput.KEY_A);
        bindKeyAction("right", KeyInput.KEY_D);
        bindMouseAction("right-click", MouseInput.BUTTON_RIGHT);
        bindMouseAction("left-click", MouseInput.BUTTON_LEFT);
    }

    private void bindKeyAction(String name, int keyCode) {
        keys.addMapping(name, new KeyTrigger(keyCode));
        keys.addListener(actionListener, name);
    }

    private void bindMouseAction(String name, int buttonCode) {
        keys.addMapping(name, new MouseButtonTrigger(buttonCode)); // Use MouseButtonTrigger for mouse events
        keys.addListener(actionListener, name);
    }
}
