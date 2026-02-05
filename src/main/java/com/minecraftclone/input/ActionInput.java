package com.minecraftclone.input;

import com.jme3.input.controls.ActionListener;

public class ActionInput implements ActionListener {

    private boolean forward, backward, left, right, jump, mouseWheelUp, mouseWheelDown, one, two, three, four, five, six, seven, eight, nine;

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "forward" -> forward = isPressed;
            case "back" -> backward = isPressed;
            case "left" -> left = isPressed;
            case "right" -> right = isPressed;
            case "jump" -> jump = isPressed;
            case "MouseWheelUp" -> mouseWheelUp = isPressed;
            case "MouseWheelDown" -> mouseWheelDown = isPressed;
            case "one" -> one = isPressed;
            case "two" -> two = isPressed;
            case "three" -> three = isPressed;
            case "four" -> four = isPressed;
            case "five" -> five = isPressed;
            case "six" -> six = isPressed;
            case "seven" -> seven = isPressed;
            case "eight" -> eight = isPressed;
            case "nine" -> nine = isPressed;
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
