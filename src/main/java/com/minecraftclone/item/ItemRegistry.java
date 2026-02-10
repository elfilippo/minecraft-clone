package com.minecraftclone.item;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    public static Map<String, Item> loadItems() {
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get("items.json"))) {
            // Step 1: Read JSON into a Map<String, Item>
            Type type = new TypeToken<Map<String, Item>>() {}.getType();
            Map<String, Item> rawItems = gson.fromJson(reader, type);

            // Step 2: Assign the JSON key as the item's ID
            Map<String, Item> itemsWithId = new HashMap<>();
            for (Map.Entry<String, Item> entry : rawItems.entrySet()) {
                String id = entry.getKey();
                Item item = entry.getValue();

                // Make a new Item object that includes the ID
                Item itemWithId = new Item(
                    id,
                    item.getStack_size(),
                    item.getDamage(),
                    item.getDurability(),
                    item.getTexture()
                );
                itemsWithId.put(id, itemWithId);
            }

            return itemsWithId;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>(); // return empty map on error
        }
    }
}
