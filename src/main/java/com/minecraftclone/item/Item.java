package com.minecraftclone.item;

public class Item {

    private String id;
    private int stack_size;
    private int damage;
    private int durability;
    private String texture;

    public Item(String id, int stack_size, int damage, int durability, String texture) {
        this.id = id;
        this.stack_size = stack_size;
        this.damage = damage;
        this.durability = durability;
        this.texture = texture;
    }

    public String getId() {
        return id;
    }

    public int getStack_size() {
        return stack_size;
    }

    public int getDamage() {
        return damage;
    }

    public int getDurability() {
        return durability;
    }

    public String getTexture() {
        return texture;
    }
}
