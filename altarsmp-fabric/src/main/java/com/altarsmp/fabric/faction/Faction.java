package com.altarsmp.fabric.faction;

/** Four factions, exactly as the original tags: vampire / pale / hyperion / human. */
public enum Faction {
    HUMAN("human", false, false),
    VAMPIRE("vampire", true, false),
    PALE("pale", true, false),
    HYPERION("hyperion", false, true);

    public final String tag;
    public final boolean cursed;
    public final boolean holy;

    Faction(String tag, boolean cursed, boolean holy) {
        this.tag = tag; this.cursed = cursed; this.holy = holy;
    }

    public static Faction byTag(String tag) {
        if (tag == null) return null;
        for (Faction f : values()) if (f.tag.equalsIgnoreCase(tag)) return f;
        return null;
    }

    public static Faction byName(String name) {
        for (Faction f : values()) if (f.name().equalsIgnoreCase(name)) return f;
        return null;
    }
}
