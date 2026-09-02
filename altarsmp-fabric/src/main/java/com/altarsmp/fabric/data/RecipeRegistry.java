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

/** Loads the authoritative 41-recipe table. */
public final class RecipeRegistry {
    private final Map<String, AltarRecipe> byId = new LinkedHashMap<>();

    public void load(ClassLoader cl) {
        try (InputStream in = cl.getResourceAsStream("data/altarsmp/recipes.json")) {
            if (in == null) { AltarSMPLog.error("recipes.json missing", new IllegalStateException("missing resource")); return; }
            Map<String, Map<String, Integer>> raw = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8),
                    new TypeToken<Map<String, Map<String, Integer>>>() {}.getType());
            int missingOutput = 0;
            for (Map.Entry<String, Map<String, Integer>> e : raw.entrySet()) {
                AltarRecipe r = new AltarRecipe();
                r.id = e.getKey();
                r.ingredients.putAll(e.getValue());
                byId.put(r.id, r);
            }
            AltarSMPLog.info("Recipe registry loaded: " + byId.size() + " recipes");
        } catch (Exception e) {
            AltarSMPLog.error("Failed to load recipes.json", e);
        }
    }

    public AltarRecipe get(String id) { return byId.get(id); }
    public List<AltarRecipe> all() { return new ArrayList<>(byId.values()); }
}
