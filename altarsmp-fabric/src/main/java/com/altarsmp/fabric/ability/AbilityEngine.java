package com.altarsmp.fabric.ability;

import com.altarsmp.fabric.AltarSMPMod;
import com.altarsmp.fabric.item.ItemIdentities;
import com.altarsmp.fabric.util.AltarSMPLog;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Routes real player interactions to the per-weapon ability implementations
 * (identity via the custom_data component, never display names).
 */
public final class AbilityEngine {
    private final Map<String, Ability> abilities = new LinkedHashMap<>();

    public void register(Ability ability) {
        abilities.put(ability.weaponId(), ability);
        AltarSMPLog.info("Ability registered: " + ability.weaponId() + " (" + ability.triggers() + ")");
    }

    public void registerEvents() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity sp && entity != null) {
                Ability a = abilityInHand(sp);
                if (a != null) {
                    a.onLeftClick(sp, sp.getMainHandStack(), entity);
                }
            }
            return ActionResult.PASS;
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player instanceof ServerPlayerEntity sp) {
                Ability a = abilityInHand(sp);
                if (a != null) a.onRightClick(sp, sp.getMainHandStack());
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity sp : server.getPlayerManager().getPlayerList()) {
                Ability a = abilityInHand(sp);
                if (a != null) a.onTick(sp, sp.getMainHandStack());
            }
        });
    }

    public Ability abilityInHand(ServerPlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        String id = ItemIdentities.weaponId(stack);
        return id == null ? null : abilities.get(id);
    }

    /** Raw trigger used by the /asmp ability command (mirrors original AbilityCommand trigger 1/2). */
    public boolean trigger(ServerPlayerEntity player, int trigger) {
        Ability a = abilityInHand(player);
        if (a == null) return false;
        if (trigger == 1) a.onSwap(player, player.getMainHandStack());
        else if (trigger == 2) a.onSneak(player, player.getMainHandStack(), player.isSneaking());
        else return false;
        return true;
    }

    public int count() { return abilities.size(); }
}
