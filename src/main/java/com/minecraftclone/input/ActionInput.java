package com.minecraftclone.input;

import com.jme3.input.controls.ActionListener;

public class ActionInput implements ActionListener {

    private boolean forward;
    private boolean backward;
    private boolean left;
    private boolean right;
    private boolean jump;

    private boolean breakBlock;
    private boolean placeBlock;

    // =========================
    // CONSTRUCTOR
    // =========================

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "forward" -> forward = isPressed;
            case "back" -> backward = isPressed;
            case "left" -> left = isPressed;
            case "right" -> right = isPressed;
            case "jump" -> jump = isPressed;
            case "left-click" -> breakBlock = isPressed;
            case "right-click" -> placeBlock = isPressed;
        }
    }

    // =========================
    // MOVEMENT
    // =========================

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

    public boolean isJump() {
        return jump;
    }

    // =========================
    // BLOCK INTERACTION
    // =========================

    public boolean breakBlock() {
        return breakBlock;
    }

    public boolean placeBlock() {
        return placeBlock;
    }

    // =========================
    // INTERNAL
    // =========================

    public ActionListener getActionListener() {
        return this;
    }
}
