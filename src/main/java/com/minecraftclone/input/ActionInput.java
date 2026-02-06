package com.minecraftclone.input;

import com.jme3.input.controls.ActionListener;

public class ActionInput implements ActionListener {

    private boolean w, s, a, d, e, q, space, one, two, three, four, five, six, seven, eight, nine, mouseWheel;

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "w" -> w = isPressed;
            case "s" -> s = isPressed;
            case "a" -> a = isPressed;
            case "d" -> d = isPressed;
            case "e" -> e = isPressed;
            case "q" -> q = isPressed;
            case "space" -> space = isPressed;
            case "1" -> one = isPressed;
            case "2" -> two = isPressed;
            case "3" -> three = isPressed;
            case "4" -> four = isPressed;
            case "5" -> five = isPressed;
            case "6" -> six = isPressed;
            case "7" -> seven = isPressed;
            case "8" -> eight = isPressed;
            case "9" -> nine = isPressed;
            case "mouseWheel" -> mouseWheel = isPressed;
        }
    }

    public boolean keyDown(char key) {
        return switch (key) {
            case 'w' -> w;
            case 'a' -> a;
            case 's' -> s;
            case 'd' -> d;
            case 'e' -> e;
            case 'q' -> q;
            case '1' -> one;
            case '2' -> two;
            case '3' -> three;
            case '4' -> four;
            case '5' -> five;
            case '6' -> six;
            case '7' -> seven;
            case '8' -> eight;
            case '9' -> nine;
            case ' ' -> space;
            default -> false;
        };
    }

    public ActionListener getActionListener() {
        return this;
    }
}
