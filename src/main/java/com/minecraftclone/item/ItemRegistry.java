package com.minecraftclone.item;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class ItemRegistry {

    private static final Map<String, Item> ITEMS = new HashMap<>();

    static {
        loadItems();
    }

    public static Map<String, Item> loadItems() {
        Yaml yaml = new Yaml();
        InputStream inputStream = ItemRegistry.class.getClassLoader().getResourceAsStream("items.yml");

        Map<String, Object> data = yaml.load(inputStream);
        Map<String, Object> itemsSection = (Map<String, Object>) data.get("items");

        for (String key : itemsSection.keySet()) {
            Map<String, Object> itemData = (Map<String, Object>) itemsSection.get(key);

            Item item = new Item(
                key,
                ItemType.valueOf((String) itemData.get("type")),
                (Integer) itemData.get("maxStack"),
                (String) itemData.get("name"),
                (Integer) itemData.get("baseDurability"),
                (Integer) itemData.get("baseDamage"),
                (Integer) itemData.get("miningEfficiency")
            );

            ITEMS.put(key, item);
        }

        return ITEMS;
    }

    public static Item get(String id) {
        return ITEMS.get(id);
    }

    public static Map<String, Item> getAll() {
        return Map.copyOf(ITEMS);
    }
}
