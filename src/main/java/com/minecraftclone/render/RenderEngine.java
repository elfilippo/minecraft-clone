package com.minecraftclone.render;

import com.minecraftclone.Main;
import com.minecraftclone.gui.PlayerGUI;
import com.minecraftclone.item.ItemInstance;
import com.minecraftclone.item.ItemRegistry;
import com.minecraftclone.player.PlayerCharacter;
import java.io.IOException;

public class RenderEngine {

    PlayerGUI gui;
    Main app;
    PlayerCharacter player;

    public RenderEngine(Main app, PlayerCharacter playerCharacter) {
        this.app = app;
        this.player = playerCharacter;
        try {
            this.gui = new PlayerGUI(app);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gui.inventoryDisplayItem(1, 9, new ItemInstance(ItemRegistry.get("iron_sword")));
        gui.inventoryDisplayItem(4, 9, new ItemInstance(ItemRegistry.get("iron_sword")));
    }

    public void guiUpdate() {
        gui.setLife(player.getLife());
        gui.setHunger(player.getHunger());
        gui.changeHotbarSlot(player.getHotbarSlot());
        gui.setInventoryVisibility(player.getinventoryVisible());
    }
}
