package com.minecraftclone.item;

import com.minecraftclone.Registries;

public final class ItemBootstrap {

    public static void registerItems() {
        Registries.ITEMS.register(new Item("stick", 64, null));
        Registries.ITEMS.register(new Item("diamond_sword", 1, 250));
    }
}
