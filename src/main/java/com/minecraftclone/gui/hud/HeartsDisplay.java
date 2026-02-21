package com.minecraftclone.gui.hud;

import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.minecraftclone.gui.display.Display;
import com.minecraftclone.util.UIHelper;
import java.util.ArrayList;
import java.util.List;

public class HeartsDisplay {

    private List<Display> hearts = new ArrayList<>();

    private Texture2D full, half;

    protected HeartsDisplay(UIHelper helper, int x, int y, Node node) {
        this.full = helper.loadGUITexture2d("sprites/hud/heart/full");
        this.half = helper.loadGUITexture2d("sprites/hud/heart/half");

        for (int i = 0; i < 10; i++) {
            Display heartContainer = new Display(
                helper,
                helper.loadGUITexture2d("sprites/hud/heart/container"),
                x + 8 * helper.getScale() * i,
                y
            );
            heartContainer.attachTo(node);

            Display heart = new Display(helper, helper.loadGUITexture2d("sprites/hud/heart/full"), x + 8 * helper.getScale() * i, y);
            hearts.add(heart);
            heart.attachTo(node);
        }
    }

    protected void setLife(int life) {
        int fullHearts = life / 2;
        boolean halfHeart = (life % 2 == 1);

        for (int i = 0; i < hearts.size(); i++) {
            Display heart = hearts.get(i);

            if (i < fullHearts) {
                heart.setVisible(true);
                heart.setTexture(full);
            } else if (i == fullHearts && halfHeart) {
                heart.setVisible(true);
                heart.setTexture(half);
            } else {
                heart.setVisible(false);
            }
        }
    }
}
