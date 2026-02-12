package com.minecraftclone.item;

import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class ItemRegistry {

    private static final Map<String, Item> ITEMS = new HashMap<>();
    Yaml yaml = new Yaml();
}
