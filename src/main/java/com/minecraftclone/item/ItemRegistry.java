package com.minecraftclone.item;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private final Map<String, Item> items = new HashMap<>();

    public void register(Item item) {
        items.put(item.getId(), item);
    }

    public Item get(String id) {
        Item item = items.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Unknown item: " + id);
        }
        return item;
    }
}
