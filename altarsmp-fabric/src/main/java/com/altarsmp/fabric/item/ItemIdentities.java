package com.altarsmp.fabric.item;

import com.altarsmp.fabric.util.AltarIds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * Item identity = vanilla {@code minecraft:custom_data} component:
 * {@code {altarsmp:{weapon:"<id>",kills:N,season:N,custom_item:"<key>",altar:"<id>"}}}.
 * Never relies on display names (original PDC semantics, translated to components).
 */
public final class ItemIdentities {
    private ItemIdentities() {}

    public static NbtCompound altarSmpData(ItemStack stack) {
        NbtCompound root = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, new NbtCompound());
        return root.contains("altarsmp") ? root.getCompound("altarsmp") : new NbtCompound();
    }

    public static void setAltarSmpData(ItemStack stack, NbtCompound as) {
        NbtCompound root = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, new NbtCompound()).copy();
        root.put("altarsmp", as);
        stack.set(DataComponentTypes.CUSTOM_DATA, root);
    }

    public static String weaponId(ItemStack stack) {
        if (stack == null) return null;
        NbtCompound as = altarSmpData(stack);
        return as.contains(AltarIds.WEAPON_KEY) ? as.getString(AltarIds.WEAPON_KEY) : null;
    }

    public static boolean isAltarWeapon(ItemStack stack) { return weaponId(stack) != null; }

    public static void setWeapon(ItemStack stack, String id, int season) {
        NbtCompound as = altarSmpData(stack).copy();
        as.putString(AltarIds.WEAPON_KEY, id);
        as.putInt(AltarIds.SEASON_KEY, season);
        if (!as.contains(AltarIds.KILLS_KEY)) as.putInt(AltarIds.KILLS_KEY, 0);
        setAltarSmpData(stack, as);
    }

    public static int kills(ItemStack stack) {
        NbtCompound as = altarSmpData(stack);
        return as.contains(AltarIds.KILLS_KEY) ? as.getInt(AltarIds.KILLS_KEY) : 0;
    }

    public static void setKills(ItemStack stack, int kills) {
        NbtCompound as = altarSmpData(stack).copy();
        as.putInt(AltarIds.KILLS_KEY, kills);
        setAltarSmpData(stack, as);
    }

    public static void addKill(ItemStack stack) { setKills(stack, kills(stack) + 1); }

    public static boolean isCustomItem(ItemStack stack, String key) {
        if (stack == null) return false;
        NbtCompound as = altarSmpData(stack);
        return as.contains(AltarIds.CUSTOM_ITEM_KEY) && key.equals(as.getString(AltarIds.CUSTOM_ITEM_KEY));
    }

    public static void setCustomItem(ItemStack stack, String key) {
        NbtCompound as = altarSmpData(stack).copy();
        as.putString(AltarIds.CUSTOM_ITEM_KEY, key);
        setAltarSmpData(stack, as);
    }

    public static boolean isAltarStandMarker(ItemStack stack, String altarId) {
        NbtCompound as = altarSmpData(stack);
        return as.contains(AltarIds.ALTAR_KEY) && altarId.equals(as.getString(AltarIds.ALTAR_KEY));
    }
}
