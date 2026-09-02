package com.altarsmp.fabric.data;

import java.util.List;

/** Authoritative weapon identity + presentation data (from data/altarsmp/weapons.json). */
public class WeaponDefinition {
    public String id;
    public String name;
    public String material;       // minecraft item id (e.g. "netherite_sword")
    public int cmd;               // custom model data float
    public String wpnClass;       // original source class name (reference only)
    public List<String> configKeys = List.of();
    public int season = 1;
    public boolean ownerBound = false; // Bow of Deception

    /** Canonical id alias map from the original (source-defined spellings). */
    public static String canonical(String raw) {
        if (raw == null) return null;
        return switch (raw.trim().toLowerCase()) {
            case "paladin_axe", "paladins_battle_axe", "paladin axe" -> "paladinbattleaxe";
            case "vulcans_crossbow", "vulcans crossbow" -> "vulcanscrossbow";
            case "eclipse", "eclipse sword", "eclipsesword" -> "eclipsesword";
            case "ancientblade", "ancient blade" -> "ancient_blade";
            case "bow of deception", "bowofdeceptionandlies" -> "bowofdeception";
            default -> raw.trim().toLowerCase().replace(' ', '_');
        };
    }
}
