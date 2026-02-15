package com.minecraftclone.player.input;

import com.jme3.input.controls.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ActionInput implements ActionListener {

    private final Map<Action, InputState> actions = new HashMap<>();

    //DOES: put every player input into the actions map
    public ActionInput() {
        for (Action action : Action.values()) {
            actions.put(action, new InputState());
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        //?: what does this do and why?
        actions.get(Action.valueOf(name)).update(isPressed);
    }

    /**
     * returns if the Specified Keybinding is held
     * @param action
     * @return
     */
    public boolean isHeld(Action action) {
        return actions.get(action).isHeld();
    }

    /**
     * returns if the Specified Keybinding is tapped
     * @param action
     * @return
     */
    public boolean isTapped(Action action) {
        return actions.get(action).consumeTap();
    }
}
