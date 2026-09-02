package com.altarsmp.fabric.util;

import net.minecraft.util.Identifier;

/** Central namespace + tag/component key constants (single source of truth for identities). */
public final class AltarIds {
    public static final String NS = "altarsmp";
    /** S2 items share the same namespace; this key is stored in item custom data. */
    public static final String WEAPON_KEY = "weapon";
    public static final String KILLS_KEY = "kills";
    public static final String SEASON_KEY = "season";
    public static final String ALTAR_KEY = "altar";
    public static final String CUSTOM_ITEM_KEY = "custom_item";
    public static final String ALTAR_TAG = "asmp_altar";
    public static final String ALTAR_TAG_PREFIX = "asmp_altar_";

    private AltarIds() {}

    public static Identifier id(String path) { return Identifier.of(NS, path); }
    public static String altarTag(String altarId) { return ALTAR_TAG_PREFIX + altarId; }
}
