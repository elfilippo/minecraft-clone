package com.minecraftclone.player.input;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.minecraftclone.player.PlayerCommand;
import java.util.HashMap;
import java.util.Map;

public class ActionInput implements ActionListener, AnalogListener {

    private final Map<Action, InputState> actions = new HashMap<>();
    private float mouseWheelUp;
    private float mouseWheelDown;

    //DOES: Puts every Playerinput into the Hashmap actions
    public ActionInput() {
        for (Action action : Action.values()) {
            actions.put(action, new InputState());
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        actions.get(Action.valueOf(name)).update(isPressed);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch (name) {
            case "INVENTORY_SLOT_UP" -> mouseWheelUp = value;
            case "INVENTORY_SLOT_DOWN" -> mouseWheelDown = value;
        }
    }

    /**
     * Returns if the Specified Keybinding is held
     * @param action
     * @return
     */
    public boolean isHeld(Action action) {
        return actions.get(action).isHeld();
    }

    /**
     * Returns if the Specified Keybinding is tapped
     * @param action
     * @return
     */
    public boolean isTapped(Action action) {
        return actions.get(action).consumeTap();
    }

    public PlayerCommand buildCommand() {
        PlayerCommand cmd = new PlayerCommand();

        if (isHeld(Action.FORWARD)) cmd.forward += 1;
        if (isHeld(Action.BACKWARD)) cmd.forward -= 1;

        if (isHeld(Action.LEFT)) cmd.strafe += 1;
        if (isHeld(Action.RIGHT)) cmd.strafe -= 1;

        if (isHeld(Action.JUMP)) cmd.jump = true;

        if (isTapped(Action.HOTBAR_1)) cmd.selectHotbar = 1;
        if (isTapped(Action.HOTBAR_2)) cmd.selectHotbar = 2;
        if (isTapped(Action.HOTBAR_3)) cmd.selectHotbar = 3;
        if (isTapped(Action.HOTBAR_4)) cmd.selectHotbar = 4;
        if (isTapped(Action.HOTBAR_5)) cmd.selectHotbar = 5;
        if (isTapped(Action.HOTBAR_6)) cmd.selectHotbar = 6;
        if (isTapped(Action.HOTBAR_7)) cmd.selectHotbar = 7;
        if (isTapped(Action.HOTBAR_8)) cmd.selectHotbar = 8;
        if (isTapped(Action.HOTBAR_9)) cmd.selectHotbar = 9;

        if (getMouseWheelUp() != 0) cmd.hotbarDelta = -1;
        if (getMouseWheelDown() != 0) cmd.hotbarDelta = +1;

        if (isTapped(Action.TOGGLE_INVENTORY)) cmd.toggleInventory = true;

        return cmd;
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
