package com.minecraftclone.input;

import com.jme3.input.controls.ActionListener;

public class ActionInput implements ActionListener {

    private boolean forward, backward, left, right, jump;

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "forward" -> forward = isPressed;
            case "back" -> backward = isPressed;
            case "left" -> left = isPressed;
            case "right" -> right = isPressed;
            case "jump" -> jump = isPressed;
        }
    }

    public boolean isJump() {
        return jump;
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
