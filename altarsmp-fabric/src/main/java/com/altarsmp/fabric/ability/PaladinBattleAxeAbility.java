package com.altarsmp.fabric.ability;

import com.altarsmp.fabric.AltarSMPMod;
import com.altarsmp.fabric.util.AltarSMPLog;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Paladin's Battle Axe — Earthshatter (swap) + Stalwart Absorption (shift-swap),
 * values read from the authoritative config keys with the original defaults.
 */
public final class PaladinBattleAxeAbility implements Ability {
    public static final String WEAPON = "paladinbattleaxe";
    private final ConcurrentHashMap<UUID, Double> absorbed = new ConcurrentHashMap<>();

    @Override public String weaponId() { return WEAPON; }
    @Override public String id() { return WEAPON; }
    @Override public List<Trigger> triggers() {
        return List.of(Trigger.SWAP, Trigger.SNEAK, Trigger.LEFT_CLICK, Trigger.TICK, Trigger.KILL);
    }

    @Override
    public void onSwap(ServerPlayerEntity player, ItemStack stack) {
        if (player.isSneaking()) useStalwart(player, stack);
        else useEarthShatter(player, stack);
    }

    @Override
    public void onSneak(ServerPlayerEntity player, ItemStack stack, boolean sneaking) {
        // Sneak alone is a state, not the original trigger; the ability fires on shift-SWAP.
    }

    @Override
    public void onLeftClick(ServerPlayerEntity player, ItemStack stack, net.minecraft.entity.Entity target) {
        // original onMeleeHit: VFX only (sound when absorbing)
        ServerWorld world = player.getServerWorld();
        if (absorbed.containsKey(player.getUuid())) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.6F, 1.8F);
        }
        Vec3d dir = player.getRotationVector().normalize();
        world.spawnParticles(ParticleTypes.CRIT,
                player.getX() + dir.x, player.getY() + 1.0 + dir.y, player.getZ() + dir.z,
                12, 0.3, 0.3, 0.3, 0.7);
    }

    @Override
    public void onKill(ServerPlayerEntity player, ItemStack stack, LivingEntity victim) {
        // no per-kill ability for this weapon (passive only)
    }

    @Override
    public void onTick(ServerPlayerEntity player, ItemStack stack) {
        int haste = AltarSMPMod.config().getInt("abilities.paladinbattleaxe.passive_haste_level", 0);
        if (haste > 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 180, haste - 1, true, false, false));
        }
    }

    private void useEarthShatter(ServerPlayerEntity player, ItemStack stack) {
        int cd = AltarSMPMod.config().getInt("abilities.paladinbattleaxe.shatter_cooldown", 45);
        if (!AltarSMPMod.cooldowns().tryUse(player.getUuid(), "paladin_earthshatter", cd)) {
            long rem = AltarSMPMod.cooldowns().remainingMs(player.getUuid(), "paladin_earthshatter", cd);
            player.sendMessage(Text.literal("Earthshatter recharging: " + (rem / 1000 + 1) + "s"), true);
            return;
        }
        ServerWorld world = player.getServerWorld();
        double radius = AltarSMPMod.config().getInt("abilities.paladinbattleaxe.shatter_radius", 6);
        double dmg = AltarSMPMod.config().getDouble("abilities.paladinbattleaxe.earth_shatter_damage", 7.0);
        double impact = AltarSMPMod.config().getDouble("abilities.paladinbattleaxe.shatter_impact_damage", 6.0);
        double knockback = AltarSMPMod.config().getDouble("abilities.paladinbattleaxe.shatter_knockback", 0.85);

        world.spawnParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 0.5, player.getZ(), 3, 0.8, 0.4, 0.8, 0.05);
        world.spawnParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + 0.5, player.getZ(), 24, 3.0, 0.2, 3.0, 0.35);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.2F);

        Box box = new Box(player.getX() - radius, player.getY() - 4, player.getZ() - radius,
                player.getX() + radius, player.getY() + 4, player.getZ() + radius);
        DamageSource source = player.getDamageSources().mobAttack(player);
        for (LivingEntity e : world.getEntitiesByType(net.minecraft.predicate.entity.TypeFilter.instanceOf(LivingEntity.class), box, le -> le != player)) {
            if (e.distanceTo(player) <= radius) {
                e.damage(source, (float) dmg);
                e.setVelocity(e.getVelocity().x, knockback, e.getVelocity().z);
                AltarSMPLog.debug("earthshatter: hit " + e.getType() + " dmg=" + dmg);
            }
        }
        // impact wave slightly farther (original: repeated near-entity damage at 10-wide box)
        Box near = new Box(player.getX() - 10, player.getY() - 4, player.getZ() - 10,
                player.getX() + 10, player.getY() + 4, player.getZ() + 10);
        for (LivingEntity e : world.getEntitiesByType(net.minecraft.predicate.entity.TypeFilter.instanceOf(LivingEntity.class), near, le -> le != player)) {
            if (e.distanceTo(player) <= 10 && !player.getServerWorld().isRaining()) {
                e.damage(source, (float) impact);
            }
        }
        player.sendMessage(Text.literal("Earthshatter!"), true);
        AltarSMPLog.info("ability: " + player.getGameProfile().getName() + " paladin_earthshatter");
    }

    private void useStalwart(ServerPlayerEntity player, ItemStack stack) {
        int cd = AltarSMPMod.config().getInt("abilities.paladinbattleaxe.stalwart_cooldown", 45);
        if (!AltarSMPMod.cooldowns().tryUse(player.getUuid(), "paladin_stalwart", cd)) {
            long rem = AltarSMPMod.cooldowns().remainingMs(player.getUuid(), "paladin_stalwart", cd);
            player.sendMessage(Text.literal("Stalwart recharging: " + (rem / 1000 + 1) + "s"), true);
            return;
        }
        int duration = AltarSMPMod.config().getInt("abilities.paladinbattleaxe.stalwart_duration", 100);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, 0, true, false, false));
        absorbed.put(player.getUuid(), 0.0);
        player.sendMessage(Text.literal("Stalwart Absorption active for " + (duration / 20) + "s"), true);
        AltarSMPLog.info("ability: " + player.getGameProfile().getName() + " paladin_stalwart active");
        // The damage storage itself runs inside the damage listener; release implemented in
        // DamageListener (absorbed map); duration expiry handled by the effect end.
    }

    public double storeDamage(ServerPlayerEntity player, float amount) {
        double cap = AltarSMPMod.config().getDouble("abilities.paladinbattleaxe.stalwart_damage_cap", 60.0);
        double ratio = AltarSMPMod.config().getDouble("abilities.paladinbattleaxe.stalwart_absorption_ratio", 0.5);
        double now = Math.min(cap, absorbed.getOrDefault(player.getUuid(), 0.0) + amount * ratio);
        absorbed.put(player.getUuid(), now);
        return now;
    }
}
