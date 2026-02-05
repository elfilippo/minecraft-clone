package com.minecraftclone.input;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;

public class KeyMapping {

    private InputManager input;
    private ActionListener actionListener;

    public KeyMapping(InputManager input, ActionListener actionListener) {
        this.input = input;
        this.actionListener = actionListener;

        bindKey("jump", KeyInput.KEY_SPACE);
        bindKey("forward", KeyInput.KEY_W);
        bindKey("back", KeyInput.KEY_S);
        bindKey("left", KeyInput.KEY_A);
        bindKey("right", KeyInput.KEY_D);
        bindKey("one", KeyInput.KEY_1);
        bindKey("two", KeyInput.KEY_2);
        bindKey("three", KeyInput.KEY_3);
        bindKey("four", KeyInput.KEY_4);
        bindKey("five", KeyInput.KEY_5);
        bindKey("six", KeyInput.KEY_6);
        bindKey("seven", KeyInput.KEY_7);
        bindKey("eight", KeyInput.KEY_8);
        bindKey("nine", KeyInput.KEY_9);
        bindMouse("mouseWheel", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
    }

    private void bindKey(String name, int keyInput) {
        input.addMapping(name, new KeyTrigger(keyInput));
        input.addListener(actionListener, name);
    }

    private void bindMouse(String name, int mouseInput) {
        input.addMapping(name, new MouseAxisTrigger(mouseInput));
    }
}
