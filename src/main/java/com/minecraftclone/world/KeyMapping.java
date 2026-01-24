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

        bind("jump", KeyInput.KEY_SPACE);
        bind("forward", KeyInput.KEY_W);
        bind("back", KeyInput.KEY_S);
        bind("left", KeyInput.KEY_A);
        bind("right", KeyInput.KEY_D);
    }

    private void bind(String name, int keyCode) {
        keys.addMapping(name, new KeyTrigger(keyCode));
        keys.addListener(actionListener, name);
    }
}
