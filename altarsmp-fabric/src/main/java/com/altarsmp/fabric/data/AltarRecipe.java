package com.altarsmp.fabric.data;

import java.util.Map;

/** One altar recipe: ingredient key (material id / custom_* / PLAYER_HEAD) -> required count. */
public class AltarRecipe {
    public String id;
    public Map<String, Integer> ingredients = new java.util.LinkedHashMap<>();

    /** DRAGON_EGG is validated for presence but never consumed (original behavior). */
    public static boolean isKept(String ingredient) {
        return "DRAGON_EGG".equalsIgnoreCase(ingredient);
    }
}
