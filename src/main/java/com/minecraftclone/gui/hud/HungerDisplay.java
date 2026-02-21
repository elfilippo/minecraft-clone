package com.minecraftclone.gui.hud;

import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.Display;
import java.util.ArrayList;
import java.util.List;

public class HungerDisplay {

    private List<Display> hungerBars = new ArrayList<>();

    private Texture2D full, half;

    protected HungerDisplay(GUIManager guiManager, int x, int y, Node node) {
        this.full = guiManager.loadGUITexture2d("sprites/hud/food_full");
        this.half = guiManager.loadGUITexture2d("sprites/hud/food_half");

        for (int i = 0; i < 10; i++) {
            Display hungerContainer = new Display(
                guiManager,
                guiManager.loadGUITexture2d("sprites/hud/food_empty"),
                x - 8 * guiManager.getScale() * i,
                y
            );
            hungerContainer.attachTo(node);

            Display hunger = new Display(guiManager, full, x - 8 * guiManager.getScale() * i, y);
            hungerBars.add(hunger);
            hunger.attachTo(node);
        }
    }

    protected void setHunger(int life) {
        int fullHunger = life / 2;
        boolean halfHunger = (life % 2 == 1);

        for (int i = 0; i < hungerBars.size(); i++) {
            Display hunger = hungerBars.get(i);

            if (i < fullHunger) {
                hunger.setVisible(true);
                hunger.setTexture(full);
            } else if (i == fullHunger && halfHunger) {
                hunger.setVisible(true);
                hunger.setTexture(half);
            } else {
                hunger.setVisible(false);
            }
        }
    }
}
