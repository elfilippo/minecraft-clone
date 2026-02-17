package com.minecraftclone.player;

import com.minecraftclone.item.ItemInstance;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    List<ItemInstance> inventory = new ArrayList<>(36);

    public List<ItemInstance> getInventory() {
        return inventory;
    }
}
