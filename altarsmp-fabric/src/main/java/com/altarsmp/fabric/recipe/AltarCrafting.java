package com.altarsmp.fabric.recipe;

import com.altarsmp.fabric.AltarSMPMod;
import com.altarsmp.fabric.data.AltarRecipe;
import com.altarsmp.fabric.data.CustomItem;
import com.altarsmp.fabric.data.WeaponDefinition;
import com.altarsmp.fabric.item.AltarItems;
import com.altarsmp.fabric.item.ItemIdentities;
import com.altarsmp.fabric.util.AltarIds;
import com.altarsmp.fabric.util.AltarSMPLog;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Transactional altar crafting (exact original semantics):
 *  1. resolve recipe
 *  2. verify EVERY ingredient (materials without custom name, custom items by identity+name, PLAYER_HEAD)
 *  3. consume ingredients (DRAGON_EGG never consumed)
 *  4. create the real output item with identity/components
 *  5. add to inventory; nothing consumed on failure.
 */
public final class AltarCrafting {
    private AltarCrafting() {}

    public static CraftingResult craft(ServerPlayerEntity player, String recipeId) {
        AltarRecipe recipe = AltarSMPMod.recipes().get(recipeId);
        if (recipe == null) {
            return new CraftingResult(CraftingResult.Status.UNKNOWN_RECIPE, List.of(), recipeId);
        }
        WeaponDefinition out = AltarSMPMod.weapons().get(recipeId);
        if (out == null) {
            return new CraftingResult(CraftingResult.Status.UNKNOWN_RECIPE, List.of(), recipeId);
        }
        AltarSMPLog.debug("craft: " + player.getGameProfile().getName() + " recipe=" + recipeId);

        List<String> missing = missingIngredients(player, recipe);
        if (!missing.isEmpty()) {
            AltarSMPLog.debug("craft: " + recipeId + " missing=" + missing);
            return CraftingResult.missing(missing);
        }

        consume(player, recipe);

        ItemStack result;
        try {
            result = AltarItems.createWeapon(out);
        } catch (Exception e) {
            AltarSMPLog.error("craft: failed to create output for " + recipeId, e);
            return new CraftingResult(CraftingResult.Status.ERROR, List.of(), recipeId);
        }
        int leftover = player.getInventory().addStack(result);
        if (leftover > 0) {
            // Inventory had no full slot for the crafted item: refund consumption is not possible
            // item-wise, so drop the crafted item at the player (original plugin used addItem too).
            player.dropItem(result, false);
            AltarSMPLog.warn("craft: " + recipeId + " inventory full, output dropped at player");
        }

        AltarSMPLog.info("craft OK: " + player.getGameProfile().getName() + " -> " + recipeId);
        return CraftingResult.ok(recipeId);
    }

    public static List<String> missingIngredients(ServerPlayerEntity player, AltarRecipe recipe) {
        List<String> missing = new ArrayList<>();
        for (Map.Entry<String, Integer> e : recipe.ingredients.entrySet()) {
            String key = e.getKey();
            int needed = e.getValue();
            if (needed <= 0) continue;
            int have = count(player, key);
            if (have < needed) {
                missing.add(key + " x" + (needed - have));
            }
        }
        return missing;
    }

    private static void consume(ServerPlayerEntity player, AltarRecipe recipe) {
        for (Map.Entry<String, Integer> e : recipe.ingredients.entrySet()) {
            String key = e.getKey();
            int needed = e.getValue();
            if (needed <= 0 || AltarRecipe.isKept(key)) continue;
            remove(player, key, needed);
        }
    }

    private static int count(ServerPlayerEntity player, String key) {
        int total = 0;
        List<ItemStack> main = player.getInventory().main;
        for (ItemStack stack : main) {
            if (matches(stack, key)) total += stack.getCount();
        }
        return total;
    }

    private static void remove(ServerPlayerEntity player, String key, int amount) {
        for (ItemStack stack : player.getInventory().main) {
            if (amount <= 0) break;
            if (!matches(stack, key)) continue;
            int take = Math.min(amount, stack.getCount());
            stack.decrement(take);
            amount -= take;
        }
    }

    /** Ingredient matching rules (from a/a.java + a/b.java): */
    static boolean matches(ItemStack stack, String key) {
        if (stack == null || stack.isEmpty()) return false;
        if (key.startsWith("custom_")) {
            // 1) identity component first (port-generated custom items)
            if (ItemIdentities.isCustomItem(stack, key)) return true;
            // 2) original fallback: display-name match with name map
            String display = AltarSMPMod.customNames().get(key);
            if (display != null) {
                Text name = stack.get(DataComponentTypes.CUSTOM_NAME);
                if (name != null && display.equalsIgnoreCase(stripMeta(name.getString()))) return true;
            }
            return false;
        }
        if ("PLAYER_HEAD".equals(key)) {
            if (!"minecraft:player_head".equals(Registries.ITEM.getId(stack.getItem()).toString())) return false;
            Text name = stack.get(DataComponentTypes.CUSTOM_NAME);
            return name != null && name.getString().contains("Head");
        }
        // plain material: must match AND have no custom display name (original gotName check)
        String expected = "minecraft:" + key.toLowerCase();
        if (!expected.equals(Registries.ITEM.getId(stack.getItem()).toString())) return false;
        return stack.get(DataComponentTypes.CUSTOM_NAME) == null && stack.get(DataComponentTypes.ITEM_NAME) == null;
    }

    private static String stripMeta(String s) {
        return s.replace("\u00a7.", "").trim();
    }

    public static String describe(CraftingResult r) {
        return switch (r.status()) {
            case OK -> "Crafted: " + r.weaponId();
            case UNKNOWN_RECIPE -> "Unknown altar recipe: " + r.weaponId();
            case MISSING_ITEMS -> "Missing items: " + String.join(", ", r.missing());
            case INVENTORY_FULL -> "Inventory full; output dropped at your feet.";
            case ERROR -> "Crafting failed (see server log).";
            default -> r.status().name();
        };
    }

    /** Convenience for the command's custom item support (uses registry). */
    public static CustomItem customItem(String key) {
        return AltarSMPMod.customItems().get(key);
    }
}
