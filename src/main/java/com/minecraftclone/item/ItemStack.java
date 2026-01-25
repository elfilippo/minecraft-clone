package com.minecraftclone.item;

import com.minecraftclone.Registries;

public class ItemStack {

    private final String itemId;
    private int count;
    private Integer durability;

    public ItemStack(String itemId, int count) {
        this.itemId = itemId;
        this.count = count;

        Item def = Registries.ITEMS.get(itemId);
        if (def.getMaxDurability() != null) {
            this.durability = def.getMaxDurability();
        }
    }

    public Item getDefinition() {
        return Registries.ITEMS.get(itemId);
    }

    public int getCount() {
        return count;
    }

    public void decrement(int amount) {
        count -= amount;
        if (count < 0) count = 0;
    }
}
