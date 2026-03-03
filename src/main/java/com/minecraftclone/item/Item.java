package com.minecraftclone.item;

public class Item {

    //INFO: simple item class with getters

    private String id;
    private ItemType type;
    private String name;

    private int maxStack;
    private int baseDamage;
    private int baseDurability;
    private int baseMiningEfficiency;

    public Item(
        String id,
        ItemType type,
        String name,
        Integer maxStack,
        Integer baseDamage,
        Integer baseDurability,
        Integer baseMiningEfficiency
    ) {
        this.id = id;
        this.type = type;
        this.name = name;

        this.maxStack = maxStack != null ? maxStack : 64;
        this.baseDurability = baseDurability != null ? baseDurability : -1; //?: If value is 0 something breaks if value is -1 it does not have durability
        this.baseDamage = baseDamage != null ? baseDamage : 1;
        this.baseMiningEfficiency = baseMiningEfficiency != null ? baseMiningEfficiency : 1;
    }

    public String getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getBaseDurability() {
        return baseDurability;
    }

    public int getBaseMiningEfficiency() {
        return baseMiningEfficiency;
    }
}
