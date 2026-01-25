package com.minecraftclone.item;

public final class Item {

    private final String id;
    private final int maxStackSize;
    private final Integer maxDurability; // null = kein Tool

    public Item(String id, int maxStackSize, Integer maxDurability) {
        this.id = id;
        this.maxStackSize = maxStackSize;
        this.maxDurability = maxDurability;
    }

    public String getId() {
        return id;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public Integer getMaxDurability() {
        return maxDurability;
    }
}
