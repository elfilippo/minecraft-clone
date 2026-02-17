package com.minecraftclone.item;

public class ItemInstance {

    private String id;
    private ItemType type;
    private int stackSize;
    private String name;
    private int damage;
    private int durability;
    private int miningEfficiency;

    public ItemInstance(Item base) {
        this.id = base.getId();
        this.type = base.getType();
        this.stackSize = base.getMaxStack();
        this.name = base.getName();
        this.damage = base.getBaseDamage();
        this.durability = base.getDurability();
        this.miningEfficiency = base.getMiningEfficiency();
    }

    public void subtractStackSize(int stacksize) {
        this.stackSize = this.stackSize - stacksize;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStackSize() {
        return stackSize;
    }
}
