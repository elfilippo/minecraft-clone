package com.minecraftclone.player;

import com.minecraftclone.gui.PlayerGUI;
import com.minecraftclone.item.ItemInstance;
import com.minecraftclone.item.ItemRegistry;

public class InventoryController {

    private final Inventory inventory;
    private PlayerGUI gui;

    public InventoryController(Inventory inventory, PlayerGUI playerGUI) {
        this.inventory = inventory;
        this.gui = playerGUI;

        addToInventory(new ItemInstance(ItemRegistry.get("diamond_sword"), 0));
    }

    public void addToInventory(ItemInstance item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getSlot(i).isEmpty()) {
                inventory.getSlot(i).setStack(item);
                updateView(i);
                break;
            }
        }
    }

    private void updateView(int index) {
        gui.inventoryDisplayItem(
            index,
            inventory.getSlot(index).getStack().getId(),
            inventory.getSlot(index).getStack().getAmount()
        );
    }

    public Slot getInventorySlot(int slot) {
        return inventory.getSlot(slot);
    }

    public void switchSlots(int slot1, int slot2) {
        Slot tempSlot = inventory.getSlot(slot1);

        inventory.setSlot(slot1, inventory.getSlot(slot2));
        inventory.setSlot(slot2, tempSlot);
    }
}
