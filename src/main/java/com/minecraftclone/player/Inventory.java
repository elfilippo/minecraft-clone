package com.minecraftclone.player;

import com.minecraftclone.item.ItemInstance;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    List<ItemInstance> inventory = new ArrayList<>(36);

    //TODO: Find solution to getting cursor position

    public List<ItemInstance> getInventory() {
        return inventory;
    }
}
