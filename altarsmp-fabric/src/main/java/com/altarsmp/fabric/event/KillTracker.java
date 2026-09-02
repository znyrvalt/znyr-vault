package com.altarsmp.fabric.event;

import com.altarsmp.fabric.AltarSMPMod;
import com.altarsmp.fabric.item.ItemIdentities;
import com.altarsmp.fabric.util.AltarSMPLog;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Real kill counter for AltarSMP weapons: on entity death, if the killer is a player
 * holding a weapon carrying our identity component, increment the weapon's kills in the
 * stack (persisted on the item) and write it to the server-side kill store.
 */
public final class KillTracker {
    private KillTracker() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            try {
                handleDeath(entity, damageSource);
            } catch (Exception e) {
                AltarSMPLog.error("KillTracker: failure on death of " + entity, e);
            }
        });
    }

    private static void handleDeath(LivingEntity entity, DamageSource source) {
        if (entity instanceof ServerPlayerEntity) return;
        PlayerEntity killer = source.getAttacker() instanceof PlayerEntity p ? p : null;
        if (killer == null) return;
        var stack = killer.getMainHandStack();
        if (!ItemIdentities.isAltarWeapon(stack)) return;
        ItemIdentities.addKill(stack);
        int kills = ItemIdentities.kills(stack);
        AltarSMPMod.killStore().record(killer.getUuid(), ItemIdentities.weaponId(stack), kills);
        AltarSMPLog.debug("kill: " + killer.getGameProfile().getName() + " weapon=" + ItemIdentities.weaponId(stack)
                + " entity=" + entity.getType() + " kills=" + kills);
    }
}
