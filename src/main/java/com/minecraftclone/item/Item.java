package com.minecraftclone.item;

public class Item {

    private String id;
    private ItemType type;
    private int maxStack;
    private String name;
    private int baseDamage;
    private int durability;
    private int miningEfficiency;

    public Item(String id, ItemType type, int maxStack, String name, int baseDamage, int durability, int miningEfficiency) {
        this.id = id;
        this.type = type;
        this.maxStack = maxStack;
        this.name = name;
        this.baseDamage = baseDamage;
        this.durability = durability;
        this.miningEfficiency = miningEfficiency;
    }

    public String getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public String getName() {
        return name;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getDurability() {
        return durability;
    }

    public int getMiningEfficiency() {
        return miningEfficiency;
    }
}
