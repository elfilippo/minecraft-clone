package com.minecraftclone.player;

public class Inventory {

    private final Slot[] slots;

    public Inventory(int size) {
        slots = new Slot[size];
        for (int i = 0; i < size; i++) {
            slots[i] = new Slot();
        }
    }

    public Slot getSlot(int index) {
        return slots[index];
    }

    public void setSlot(int index, Slot slot) {
        slots[index] = slot;
    }

    public int getSize() {
        return slots.length;
    }
}
