package com.minecraftclone.world;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.controls.ActionListener;

public class ActionInput implements ActionListener {

    private boolean forward, backward, left, right;
    private final CharacterControl player;

    public ActionInput(CharacterControl player) {
        this.player = player;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "forward" -> forward = isPressed;
            case "back" -> backward = isPressed;
            case "left" -> left = isPressed;
            case "right" -> right = isPressed;
            case "jump" -> {
                if (player.onGround()) player.jump();
            }
        }
    }

    public boolean isForward() {
        return forward;
    }

    public boolean isBackward() {
        return backward;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public ActionListener getActionListener() {
        return this;
    }
}
