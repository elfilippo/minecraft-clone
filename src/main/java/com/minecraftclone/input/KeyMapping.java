package com.minecraftclone.input;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;

public class KeyMapping {

    private InputManager input;
    private ActionListener actionListener;
    private AnalogListener analogListener;

    public KeyMapping(InputManager input, ActionListener actionListener, AnalogListener analogListener) {
        this.input = input;
        this.actionListener = actionListener;
        this.analogListener = analogListener;

        bindKey("space", KeyInput.KEY_SPACE);
        bindKey("w", KeyInput.KEY_W);
        bindKey("s", KeyInput.KEY_S);
        bindKey("a", KeyInput.KEY_A);
        bindKey("d", KeyInput.KEY_D);
        bindKey("e", KeyInput.KEY_E);
        bindKey("q", KeyInput.KEY_Q);
        bindKey("1", KeyInput.KEY_1);
        bindKey("2", KeyInput.KEY_2);
        bindKey("3", KeyInput.KEY_3);
        bindKey("4", KeyInput.KEY_4);
        bindKey("5", KeyInput.KEY_5);
        bindKey("6", KeyInput.KEY_6);
        bindKey("7", KeyInput.KEY_7);
        bindKey("8", KeyInput.KEY_8);
        bindKey("9", KeyInput.KEY_9);
        bindMouse("mouseWheelUp", MouseInput.AXIS_WHEEL, true);
        bindMouse("mouseWheelDown", MouseInput.AXIS_WHEEL, false);
    }

    private void bindKey(String name, int keyInput) {
        input.addMapping(name, new KeyTrigger(keyInput));
        input.addListener(actionListener, name);
    }

    private void bindMouse(String name, int mouseInput, boolean upDown) {
        input.addMapping(name, new MouseAxisTrigger(mouseInput, upDown));
        input.addListener(analogListener, name);
    }
}
