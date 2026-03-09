package com.minecraftclone.player.input;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class KeyMapping {

    private InputManager keys;
    private ActionListener actionListener;

    /**
     * initializes keymappings
     * @param keys
     * @param actionListener
     */
    public KeyMapping(InputManager keys, ActionListener actionListener) {
        this.keys = keys;
        this.actionListener = actionListener;

        bindKeyAction("FORWARD", KeyInput.KEY_W);
        bindKeyAction("BACKWARD", KeyInput.KEY_S);
        bindKeyAction("LEFT", KeyInput.KEY_A);
        bindKeyAction("RIGHT", KeyInput.KEY_D);
        bindKeyAction("JUMP", KeyInput.KEY_SPACE);

        bindKeyAction("TOGGLE_INVENTORY", KeyInput.KEY_E);
        bindKeyAction("DROP", KeyInput.KEY_Q);
        bindKeyAction("SNEAK", KeyInput.KEY_LSHIFT);
        bindKeyAction("PAUSE", KeyInput.KEY_ESCAPE);

        //DOES: iterate over hotbar names to simplify it
        for (int i = 0; i < 9; i++) {
            //INFO: KeyInput.Keyx is equal to int x+2, so 2 gets added to i to get the correct keycode
            //INFO: hotbar string names start with HOTBAR_1, so 1 gets added to i to get the correct name
            bindKeyAction("HOTBAR_" + (i + 1), i + 2);
        }

        bindMouseAction("PLACE_BLOCK", MouseInput.BUTTON_RIGHT);
        bindMouseAction("BREAK_BLOCK", MouseInput.BUTTON_LEFT);
        bindMouseAction("MENU_SELECT", MouseInput.BUTTON_LEFT);

        bindMouseAxis("INVENTORY_SLOT_DOWN", MouseInput.AXIS_WHEEL, true);
        bindMouseAxis("INVENTORY_SLOT_UP", MouseInput.AXIS_WHEEL, false);
    }

    private void bindKeyAction(String name, int keyCode) {
        keys.addMapping(name, new KeyTrigger(keyCode));
        keys.addListener(actionListener, name);
    }

    private void bindMouseAction(String name, int buttonCode) {
        keys.addMapping(name, new MouseButtonTrigger(buttonCode)); //INFO: Use MouseButtonTrigger for mouse events
        keys.addListener(actionListener, name);
    }

    private void bindMouseAxis(String name, int axisCode, boolean negative) {
        keys.addMapping(name, new MouseAxisTrigger(axisCode, negative));
        keys.addListener(actionListener, name);
    }
}
