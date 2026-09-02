package com.altarsmp.fabric.data;

import com.altarsmp.fabric.util.AltarSMPLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Loads custom ingredient identities; built from the recipe table + item source classes. */
public final class CustomItemRegistry {
    private final Map<String, CustomItem> byKey = new LinkedHashMap<>();

    public void load(ClassLoader cl) {
        try (InputStream in = cl.getResourceAsStream("data/altarsmp/custom_items.json")) {
            if (in == null) { AltarSMPLog.error("custom_items.json missing", new IllegalStateException("missing resource")); return; }
            Map<String, Integer> present = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8),
                    new TypeToken<Map<String, Integer>>() {}.getType());
            for (String key : present.keySet()) {
                CustomItem it = new CustomItem();
                it.key = key;
                byKey.put(key, it);
            }
            AltarSMPLog.info("Custom item registry loaded: " + byKey.size() + " ingredient keys");
        } catch (Exception e) {
            AltarSMPLog.error("Failed to load custom_items.json", e);
        }
    }

    public CustomItem get(String key) { return byKey.get(key); }
    public List<CustomItem> all() { return new ArrayList<>(byKey.values()); }
}
