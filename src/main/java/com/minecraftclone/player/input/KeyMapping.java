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

        bindKeyAction("ENTER", KeyInput.KEY_RETURN);
        bindKeyAction("BACKSPACE", KeyInput.KEY_BACK);
        bindKeyAction("SPACE", KeyInput.KEY_SPACE);

        bindKeyAction("A", KeyInput.KEY_A);
        bindKeyAction("B", KeyInput.KEY_B);
        bindKeyAction("C", KeyInput.KEY_C);
        bindKeyAction("D", KeyInput.KEY_D);
        bindKeyAction("E", KeyInput.KEY_E);
        bindKeyAction("F", KeyInput.KEY_F);
        bindKeyAction("G", KeyInput.KEY_G);
        bindKeyAction("H", KeyInput.KEY_H);
        bindKeyAction("I", KeyInput.KEY_I);
        bindKeyAction("J", KeyInput.KEY_J);
        bindKeyAction("K", KeyInput.KEY_K);
        bindKeyAction("L", KeyInput.KEY_L);
        bindKeyAction("M", KeyInput.KEY_M);
        bindKeyAction("N", KeyInput.KEY_N);
        bindKeyAction("O", KeyInput.KEY_O);
        bindKeyAction("P", KeyInput.KEY_P);
        bindKeyAction("Q", KeyInput.KEY_Q);
        bindKeyAction("R", KeyInput.KEY_R);
        bindKeyAction("S", KeyInput.KEY_S);
        bindKeyAction("T", KeyInput.KEY_T);
        bindKeyAction("U", KeyInput.KEY_U);
        bindKeyAction("V", KeyInput.KEY_V);
        bindKeyAction("W", KeyInput.KEY_W);
        bindKeyAction("X", KeyInput.KEY_X);
        bindKeyAction("Y", KeyInput.KEY_Y);
        bindKeyAction("Z", KeyInput.KEY_Z);

        bindKeyAction("ONE", KeyInput.KEY_1);
        bindKeyAction("TWO", KeyInput.KEY_2);
        bindKeyAction("THREE", KeyInput.KEY_3);
        bindKeyAction("FOUR", KeyInput.KEY_4);
        bindKeyAction("FIVE", KeyInput.KEY_5);
        bindKeyAction("SIX", KeyInput.KEY_6);
        bindKeyAction("SEVEN", KeyInput.KEY_7);
        bindKeyAction("EIGHT", KeyInput.KEY_8);
        bindKeyAction("NINE", KeyInput.KEY_9);
        bindKeyAction("ZERO", KeyInput.KEY_0);

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
