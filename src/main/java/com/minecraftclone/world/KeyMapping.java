package com.minecraftclone.world;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class KeyMapping {

    private InputManager keys;
    private ActionListener actionListener;

    public KeyMapping(InputManager keys, ActionListener actionListener) {
        this.keys = keys;
        this.actionListener = actionListener;

        bindAction("jump", KeyInput.KEY_SPACE);
        bindAction("forward", KeyInput.KEY_W);
        bindAction("back", KeyInput.KEY_S);
        bindAction("left", KeyInput.KEY_A);
        bindAction("right", KeyInput.KEY_D);
    }

    private void bindAction(String name, int keyCode) {
        keys.addMapping(name, new KeyTrigger(keyCode));
        keys.addListener(actionListener, name);
    }
}
