package com.altarsmp.fabric.protection;

import com.altarsmp.fabric.ability.PaladinBattleAxeAbility;
import com.altarsmp.fabric.util.AltarSMPLog;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Stalwart absorption: while the Paladin Battle Axe effect is active, incoming damage is
 * stored (capped) in the ability's absorber; reading the resistance effect is used as the
 * active marker (the effect is applied by the ability on activation).
 */
public final class DamageListener {
    private final PaladinBattleAxeAbility axe;

    public DamageListener(PaladinBattleAxeAbility axe) {
        this.axe = axe;
    }

    public void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (entity instanceof ServerPlayerEntity sp && hasStalwart(sp)) {
                try {
                    axe.storeDamage(sp, damageTaken);
                    AltarSMPLog.debug("stalwart stored " + damageTaken + " for " + sp.getGameProfile().getName());
                } catch (Exception e) {
                    AltarSMPLog.error("stalwart tracking failed", e);
                }
            }
        });
    }

    private static boolean hasStalwart(ServerPlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.RESISTANCE);
    }
}
