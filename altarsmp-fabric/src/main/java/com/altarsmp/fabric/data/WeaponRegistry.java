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

/** Loads the authoritative weapon table (ship data). */
public final class WeaponRegistry {
    private final Map<String, WeaponDefinition> byId = new LinkedHashMap<>();
    private int loaded = 0;

    public void load(ClassLoader cl) {
        try (InputStream in = cl.getResourceAsStream("data/altarsmp/weapons.json")) {
            if (in == null) { AltarSMPLog.error("weapons.json missing from mod resources", new IllegalStateException("missing resource")); return; }
            List<WeaponDefinition> list = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8),
                    new TypeToken<List<WeaponDefinition>>() {}.getType());
            for (WeaponDefinition w : list) {
                String key = WeaponDefinition.canonical(w.id);
                byId.put(key, w);
                loaded++;
            }
            AltarSMPLog.info("Weapon registry loaded: " + loaded + " weapons (" + byId.size() + " canonical ids)");
        } catch (Exception e) {
            AltarSMPLog.error("Failed to load weapons.json", e);
        }
    }

    public WeaponDefinition get(String rawId) { return byId.get(WeaponDefinition.canonical(rawId)); }
    public List<WeaponDefinition> all() { return new ArrayList<>(byId.values()); }
    public Map<String, WeaponDefinition> byId() { return byId; }
}
