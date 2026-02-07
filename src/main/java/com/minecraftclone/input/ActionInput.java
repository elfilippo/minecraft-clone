package com.minecraftclone.input;

import com.jme3.input.controls.ActionListener;

public class ActionInput implements ActionListener {

    private boolean forward;
    private boolean backward;
    private boolean left;
    private boolean right;
    private boolean jump;

    private boolean breakBlockHeld;
    private boolean placeBlockHeld;

    // These track taps
    private boolean breakBlockTapped;
    private boolean placeBlockTapped;

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
            case "left-click" -> {
                if (isPressed && !breakBlockHeld) {
                    breakBlockTapped = true; // Only true on the frame it was pressed
                }
                breakBlockHeld = isPressed;
            }
            case "right-click" -> {
                if (isPressed && !placeBlockHeld) {
                    placeBlockTapped = true;
                }
                placeBlockHeld = isPressed;
            }
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

    public boolean breakBlockHeld() {
        return breakBlockHeld;
    }

    public boolean placeBlockHeld() {
        return placeBlockHeld;
    }

    // Tap methods — reset after being read
    public boolean breakBlock() {
        boolean tapped = breakBlockTapped;
        breakBlockTapped = false; // Reset after reading
        return tapped;
    }

    public boolean placeBlock() {
        boolean tapped = placeBlockTapped;
        placeBlockTapped = false;
        return tapped;
    }

    // =========================
    // INTERNAL
    // =========================

    public ActionListener getActionListener() {
        return this;
    }
}
