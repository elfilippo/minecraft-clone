package com.minecraftclone.player;

import com.minecraftclone.item.ItemInstance;

public class InventoryController {

    private final Inventory inventory;
    private ItemInstance carriedStack; // what player holds with mouse

    public InventoryController(Inventory inventory) {
        this.inventory = inventory;
    }

    public void onSlotClicked(int index) {
        Slot slot = inventory.getSlot(index);

        if (carriedStack == null) {
            pickUp(slot);
        } else {
            placeOrSwap(slot);
        }
    }

    private void pickUp(Slot slot) {
        if (!slot.isEmpty()) {
            carriedStack = slot.getStack();
            slot.clear();
        }
    }

    private void placeOrSwap(Slot slot) {
        if (slot.isEmpty()) {
            slot.setStack(carriedStack);
            carriedStack = null;
            return;
        }

        ItemInstance slotStack = slot.getStack();

        if (slotStack.getItem() == carriedStack.getItem()) {
            int max = slotStack.getItem().getMaxStack();
            int canAdd = max - slotStack.getAmount();

            int toTransfer = Math.min(canAdd, carriedStack.getAmount());
            slotStack.add(toTransfer);
            carriedStack.remove(toTransfer);

            if (carriedStack.getAmount() <= 0) {
                carriedStack = null;
            }
        } else {
            // swap
            ItemInstance temp = slotStack;
            slot.setStack(carriedStack);
            carriedStack = temp;
        }
    }
}
