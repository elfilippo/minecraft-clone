package com.minecraftclone.player;

import com.minecraftclone.item.ItemInstance;

public class InventoryController {

    private final Inventory inventory;
    private ItemInstance carriedStack; // what player holds with mouse

    public InventoryController(Inventory inventory) {
        this.inventory = inventory;
    }

    public void addToInventory(ItemInstance item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getSlot(i).isEmpty()) {
                inventory.getSlot(i).setStack(item);
            }
        }
    }

    public Slot getInventorySlot(int slot) {
        return inventory.getSlot(slot);
    }
}
