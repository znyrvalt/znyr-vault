package com.altarsmp.fabric.data;

/** A custom crafting ingredient item: identity by custom_data key + optional display name. */
public class CustomItem {
    public String key;          // e.g. custom_weapon_handle
    public String displayName;  // e.g. "Weapon Handle" (authoritative display name)
    public String material;     // minecraft item id
    public int cmd;
    public int season = 1;
}
