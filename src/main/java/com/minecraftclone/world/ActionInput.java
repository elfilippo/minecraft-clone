package com.minecraftclone.world;

import com.jme3.bullet.control.CharacterControl;

public class ActionInput {

    public ActionInput(String name, boolean keyPressed, float tpf, CharacterControl player) {
        if (name.equals("jump") && !keyPressed) {
            player.jump();
        }
        if (name.equals("forward") && keyPressed) {
            System.out.println("forward");
        }
    }
}
