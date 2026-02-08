package com.minecraftclone.player.input;

import com.jme3.input.controls.ActionListener;

public class ActionInput implements ActionListener {

    private boolean forward;
    private boolean backward;
    private boolean left;
    private boolean right;
    private boolean jump;

    private boolean breakBlockHeld;
    private boolean placeBlockHeld;

    private boolean breakBlockTapped;
    private boolean placeBlockTapped;

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

    public boolean breakBlockHeld() {
        return breakBlockHeld;
    }

    public boolean placeBlockHeld() {
        return placeBlockHeld;
    }

    public boolean breakBlock() {
        boolean tapped = breakBlockTapped;
        breakBlockTapped = false; //INFO: reset after reading
        return tapped;
    }

    public boolean placeBlock() {
        boolean tapped = placeBlockTapped;
        placeBlockTapped = false;
        return tapped;
    }
}
