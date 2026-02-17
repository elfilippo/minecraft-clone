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

        gui.inventoryDisplayItem(1, 1, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(1, 2, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(1, 3, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(1, 4, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(1, 5, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(1, 6, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(1, 7, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(1, 8, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(1, 9, new ItemInstance(ItemRegistry.get("golden_apple")));

        gui.inventoryDisplayItem(2, 1, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(2, 2, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(2, 3, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(2, 4, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(2, 5, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(2, 6, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(2, 7, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(2, 8, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(2, 9, new ItemInstance(ItemRegistry.get("golden_apple")));

        gui.inventoryDisplayItem(3, 1, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(3, 2, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(3, 3, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(3, 4, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(3, 5, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(3, 6, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(3, 7, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(3, 8, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(3, 9, new ItemInstance(ItemRegistry.get("golden_apple")));

        gui.inventoryDisplayItem(4, 1, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(4, 2, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(4, 3, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(4, 4, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(4, 5, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(4, 6, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(4, 7, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(4, 8, new ItemInstance(ItemRegistry.get("golden_apple")));
        gui.inventoryDisplayItem(4, 9, new ItemInstance(ItemRegistry.get("golden_apple")));
    }

    public void guiUpdate() {
        gui.setLife(player.getLife());
        gui.setHunger(player.getHunger());
        gui.changeHotbarSelectedSlot(player.getHotbarSlot());
        gui.setInventoryVisibility(player.getinventoryVisible());
    }
}
