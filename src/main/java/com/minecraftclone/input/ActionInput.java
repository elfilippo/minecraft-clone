package com.minecraftclone.input;

import com.jme3.input.controls.ActionListener;

public class ActionInput implements ActionListener {

    private boolean w, s, a, d, e, q, space, one, two, three, four, five, six, seven, eight, nine;

    @Override
    public void onAction(String name, boolean pressed, float tpf) {
        switch (name) {
            case "w" -> w = pressed;
            case "s" -> s = pressed;
            case "a" -> a = pressed;
            case "d" -> d = pressed;
            case "e" -> e = pressed;
            case "q" -> q = pressed;
            case "space" -> space = pressed;
            case "1" -> one = pressed;
            case "2" -> two = pressed;
            case "3" -> three = pressed;
            case "4" -> four = pressed;
            case "5" -> five = pressed;
            case "6" -> six = pressed;
            case "7" -> seven = pressed;
            case "8" -> eight = pressed;
            case "9" -> nine = pressed;
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

    public boolean keyUp(char key) {
        return !switch (key) {
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
