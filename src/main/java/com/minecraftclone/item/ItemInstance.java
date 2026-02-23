package com.minecraftclone.item;

public class ItemInstance {

    private Item item;
    private int amount;

    public ItemInstance(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public void add(int value) {
        amount += value;
    }

    public void remove(int value) {
        amount -= value;
    }
}
