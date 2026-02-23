package com.minecraftclone.player;

import com.minecraftclone.item.ItemInstance;

public class Slot {

    private ItemInstance stack;

    public boolean isEmpty() {
        return stack == null || stack.getAmount() <= 0;
    }

    public ItemInstance getStack() {
        return stack;
    }

    public void setStack(ItemInstance stack) {
        this.stack = stack;
    }

    public void clear() {
        this.stack = null;
    }
}
