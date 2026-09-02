package com.altarsmp.fabric.item;

import com.altarsmp.fabric.data.CustomItem;
import com.altarsmp.fabric.data.WeaponDefinition;
import com.altarsmp.fabric.util.AltarIds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/** Builds real 26.2 ItemStacks with vanilla components (identity + presentation). */
public final class AltarItems {
    private AltarItems() {}

    private static ItemStack base(String material, int count) {
        Identifier id = Identifier.of("minecraft", material);
        if (!Registries.ITEM.containsId(id)) {
            throw new IllegalArgumentException("Unknown item id: " + id);
        }
        return new ItemStack(Registries.ITEM.get(id), count);
    }

    /** Original names are MiniMessage strings (gradient/rainbow). Rich text needs an external
     *  renderer; the port renders the plain text and keeps the full original in the data set. */
    private static Text plainName(String miniMessage) {
        String plain = miniMessage == null ? "" : miniMessage.replaceAll("<[^>]+>", "");
        return Text.literal(plain);
    }

    public static void applyModelData(ItemStack stack, int cmd) {
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA,
                new CustomModelDataComponent(List.of((float) cmd), (byte) 0, List.of(), List.of()));
    }

    public static ItemStack createWeapon(WeaponDefinition w) {
        ItemStack stack = base(w.material == null ? "netherite_sword" : w.material, 1);
        ItemIdentities.setWeapon(stack, w.id, w.season);
        applyModelData(stack, w.cmd);
        stack.set(DataComponentTypes.CUSTOM_NAME, plainName(w.name));
        stack.set(DataComponentTypes.ITEM_NAME, plainName(w.name));
        stack.set(DataComponentTypes.RARITY, rarityFor(w.id));
        stack.set(DataComponentTypes.LORE, new LoreComponent(defaultLore(w)));
        applyEnchants(stack, w);
        return stack;
    }

    /** Original createWeapon() enchantments from config keys (enchants.<weapon>.<enchant>). */
    private static void applyEnchants(ItemStack stack, WeaponDefinition w) {
        String[] enchants = {
                "sharpness", "looting", "sweeping_edge", "unbreaking", "mending",
                "efficiency", "smite", "power", "punch", "flame", "infinity",
                "knockback", "fire_aspect", "thorns", "fortune", "silk_touch"
        };
        for (String e : enchants) {
            int level = com.altarsmp.fabric.AltarSMPMod.config().getInt("enchants." + w.id + "." + e, 0);
            if (level <= 0) continue;
            net.minecraft.enchantment.Enchantment enchantment = switch (e) {
                case "sharpness" -> net.minecraft.enchantment.Enchantments.SHARPNESS;
                case "looting" -> net.minecraft.enchantment.Enchantments.LOOTING;
                case "sweeping_edge" -> net.minecraft.enchantment.Enchantments.SWEEPING_EDGE;
                case "unbreaking" -> net.minecraft.enchantment.Enchantments.UNBREAKING;
                case "mending" -> net.minecraft.enchantment.Enchantments.MENDING;
                case "efficiency" -> net.minecraft.enchantment.Enchantments.EFFICIENCY;
                default -> null;
            };
            if (enchantment != null) {
                stack.addEnchantment(net.minecraft.registry.RegistryEntry.of(enchantment), level);
            }
        }
    }

    private static Rarity rarityFor(String id) {
        // Original rarities: mostly UNCOMMON; S2 sets none explicitly (weapons inherit).
        return switch (id) {
            case "knightfall", "hyperion", "eclipsesword" -> Rarity.RARE;
            case "crazyslots" -> Rarity.EPIC;
            default -> Rarity.UNCOMMON;
        };
    }

    private static List<Text> defaultLore(WeaponDefinition w) {
        List<Text> lore = new ArrayList<>();
        lore.add(Text.literal("AltarSMP " + (w.season == 2 ? "Season 2" : "Season 1") + " Legendary"));
        lore.add(Text.literal("Kills: 0"));
        return lore;
    }

    public static ItemStack createCustomItem(CustomItem c) {
        ItemStack stack = base(c.material == null ? "clay_ball" : c.material, 1);
        ItemIdentities.setCustomItem(stack, c.key);
        applyModelData(stack, c.cmd);
        if (c.displayName != null) {
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(c.displayName));
            stack.set(DataComponentTypes.ITEM_NAME, Text.literal(c.displayName));
        }
        return stack;
    }

    /** Weapon give command path (id may be any source alias). */
    public static ItemStack createById(String rawId) {
        WeaponDefinition w = com.altarsmp.fabric.AltarSMPMod.weapons().get(rawId);
        if (w == null) throw new IllegalArgumentException("Unknown weapon id: " + rawId);
        return createWeapon(w);
    }
}
