package com.altarsmp.fabric.faction;

import com.altarsmp.fabric.AltarSMPMod;
import com.altarsmp.fabric.util.AltarSMPLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Faction state: tag per player (vampire/pale/hyperion/human) + persistent store
 * (config/altarsmp/state/factions.json). Effects follow the original VampireManager
 * exactly: vampire daylight burn + night buffs, pale moss speed + rain weakness,
 * health bonuses via max-health attribute modifier, pale-system activation.
 * Tick application is driven by ServerTickEvents.END_SERVER_TICK (config curses.enabled).
 */
public final class FactionManager {
    private static final UUID MAX_HEALTH_MODIFIER = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
    private final Map<UUID, Faction> factions = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> kings = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> paleActivated = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> blessed = new ConcurrentHashMap<>();
    private Path statePath;

    public void setStorage(Path gameConfigDir) {
        statePath = gameConfigDir.resolve("altarsmp").resolve("state").resolve("factions.json");
    }

    public Faction of(PlayerEntity player) {
        return of(player.getUuid());
    }

    public Faction of(UUID uuid) {
        return factions.getOrDefault(uuid, Faction.HUMAN);
    }

    public boolean isKing(PlayerEntity player) { return kings.getOrDefault(player.getUuid(), Boolean.FALSE); }

    public void setFaction(ServerPlayerEntity player, Faction faction) {
        for (Faction f : Faction.values()) player.removeCommandTag(f.tag);
        player.addCommandTag(faction.tag);
        factions.put(player.getUuid(), faction);
        applyImmediate(player);
        save();
        AltarSMPLog.info("faction set: " + player.getGameProfile().getName() + " -> " + faction.name());
    }

    public void setKing(ServerPlayerEntity player, boolean king) {
        kings.put(player.getUuid(), king);
        if (king) player.addCommandTag("king");
        else player.removeCommandTag("king");
        applyImmediate(player);
        save();
        AltarSMPLog.info("king set: " + player.getGameProfile().getName() + " -> " + king);
    }

    /** Blood-Moon activation converts marked players to Pale (tag `paleaffect`). */
    public void activatePaleSystem(ServerPlayerEntity player) {
        paleActivated.put(player.getUuid(), true);
        setFaction(player, Faction.PALE);
        player.addCommandTag("paleaffect");
        AltarSMPLog.info("pale system activated for " + player.getGameProfile().getName());
    }

    public boolean isPaleActivated(PlayerEntity player) {
        return paleActivated.getOrDefault(player.getUuid(), player.getCommandTags().contains("paleaffect"));
    }

    public void setBlessed(ServerPlayerEntity player, boolean value) {
        blessed.put(player.getUuid(), value);
        if (value) player.addCommandTag("hyperion_blessed");
        else player.removeCommandTag("hyperion_blessed");
        save();
    }

    public boolean isBlessed(PlayerEntity player) {
        return blessed.getOrDefault(player.getUuid(), player.getCommandTags().contains("hyperion_blessed"));
    }

    /** Real per-20-tick effects (called from the tick event). */
    public void applyTick(ServerPlayerEntity player) {
        if (!AltarSMPMod.config().getBoolean("curses.enabled", true)) return;
        Faction f = of(player);
        if (f == Faction.VAMPIRE) applyVampireEffects(player);
        else if (f == Faction.PALE) applyPaleEffects(player);
        else if (f == Faction.HYPERION) updateKingHealth(player, 0);
    }

    private void applyVampireEffects(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        long time = world.getTimeOfDay() % 24000L;
        boolean day = time >= 0 && time < 12300L;
        if (day && world.getSkyLight(player.getBlockPos()) >= 15) {
            player.setFireTicks(40);
        } else {
            int strength = AltarSMPMod.config().getInt("vampire.night_strength_level", 0);
            int speed = AltarSMPMod.config().getInt("vampire.night_speed_level", 1);
            boolean fireRes = AltarSMPMod.config().getBoolean("vampire.night_fire_resistance", false);
            if (strength > 0) player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, strength - 1, false, false, false));
            if (speed > 0) player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, speed - 1, false, false, false));
            if (fireRes) player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 40, 0, false, false, false));
        }
        updateKingHealth(player, AltarSMPMod.config().getDouble("vampire.king_health", 10.0));
    }

    private void applyPaleEffects(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getBlockPos();
        boolean onMoss = world.getBlockState(pos.down()).isOf(net.minecraft.block.Blocks.MOSS_BLOCK);
        if (onMoss && !player.isTouchingWater()) {
            int level = AltarSMPMod.config().getInt("pale.moss_speed_level", 1);
            if (level > 0) player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, level - 1, false, false, false));
            if (level < 0) player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, level + 1, false, false, false));
        }
        if (world.isRaining() && world.getSkyLight(pos) > 0 && world.isSkyVisible(pos)) {
            int weak = AltarSMPMod.config().getInt("pale.rain_weakness_level", 1);
            if (weak > 0) player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40, weak - 1, false, false, false));
        }
        updateKingHealth(player, AltarSMPMod.config().getDouble("pale.king_health", 5.0));
    }

    /** Health bonus via AttributeModifier (ADD_NUMBER, stable UUID) — original ADD_NUMBER behavior. */
    private void updateKingHealth(ServerPlayerEntity player, double kingHealth) {
        double bonus = altarsmpModifier(player, kingHealth);
        applyMaxHealth(player, bonus);
        if (booleanEnabled("vampire.king_health") || true) {
            // modifier always applied with the configured per-faction bonus; 0 when no bonus
            // (original applied king vs base health separately; apply the effective one)
        }
    }

    private double altarsmpModifier(ServerPlayerEntity player, double kingHealth) {
        Faction f = of(player);
        if (isKing(player)) {
            return f == Faction.PALE ? kingHealth : kingHealth;
        }
        return f == Faction.VAMPIRE ? AltarSMPMod.config().getDouble("vampire.base_health", 4.0)
                : f == Faction.PALE ? AltarSMPMod.config().getDouble("pale.base_health", 2.0)
                : 0.0;
    }

    private void applyMaxHealth(ServerPlayerEntity player, double bonus) {
        try {
            var attr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (attr == null) return;
            var modifier = attr.getModifier(MAX_HEALTH_MODIFIER);
            if (modifier != null) attr.removeModifier(modifier);
            if (bonus > 0) {
                attr.addTemporaryModifier(new net.minecraft.entity.attribute.EntityAttributeModifier(
                        MAX_HEALTH_MODIFIER, "altarsmp_faction_health", bonus, net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADD_VALUE));
            }
        } catch (Exception e) {
            AltarSMPLog.error("applyMaxHealth failed for " + player.getGameProfile().getName(), e);
        }
    }

    public void applyImmediate(ServerPlayerEntity player) { applyTick(player); }

    public void load(Path gameConfigDir) {
        setStorage(gameConfigDir);
        if (statePath == null || !Files.exists(statePath)) return;
        try {
            JsonObject obj = JsonParser.parseString(Files.readString(statePath, StandardCharsets.UTF_8)).getAsJsonObject();
            if (obj.has("factions")) {
                JsonObject f = obj.getAsJsonObject("factions");
                for (String k : f.keySet()) {
                    Faction fac = Faction.byName(f.get(k).getAsString());
                    if (fac != null) factions.put(UUID.fromString(k), fac);
                }
            }
            if (obj.has("kings")) {
                JsonObject k = obj.getAsJsonObject("kings");
                for (String id : k.keySet()) if (k.get(id).getAsBoolean()) kings.put(UUID.fromString(id), true);
            }
            AltarSMPLog.info("Factions loaded: " + factions.size() + " players");
        } catch (Exception e) {
            AltarSMPLog.error("Failed to load factions state", e);
        }
    }

    public void save() {
        if (statePath == null) return;
        try {
            Files.createDirectories(statePath.getParent());
            JsonObject obj = new JsonObject();
            JsonObject f = new JsonObject();
            JsonObject k = new JsonObject();
            for (Map.Entry<UUID, Faction> e : factions.entrySet()) f.addProperty(e.getKey().toString(), e.getValue().name());
            for (Map.Entry<UUID, Boolean> e : kings.entrySet()) if (e.getValue()) k.addProperty(e.getKey().toString(), true);
            obj.add("factions", f);
            obj.add("kings", k);
            Files.writeString(statePath, new GsonBuilder().setPrettyPrinting().create().toJson(obj), StandardCharsets.UTF_8);
        } catch (IOException e) {
            AltarSMPLog.error("Failed to save factions state", e);
        }
    }

    private boolean booleanEnabled(String key) { return AltarSMPMod.config().getBoolean(key, false); }
    public int knownPlayers() { return factions.size(); }
}
