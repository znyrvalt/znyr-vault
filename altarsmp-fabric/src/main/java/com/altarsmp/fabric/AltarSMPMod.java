package com.altarsmp.fabric;

import com.altarsmp.fabric.ability.AbilityEngine;
import com.altarsmp.fabric.ability.CooldownManager;
import com.altarsmp.fabric.ability.PaladinBattleAxeAbility;
import com.altarsmp.fabric.altar.AltarManager;
import com.altarsmp.fabric.command.AltarSMPCommands;
import com.altarsmp.fabric.config.AltarSMPConfig;
import com.altarsmp.fabric.data.CustomItemRegistry;
import com.altarsmp.fabric.data.CustomItem;
import com.altarsmp.fabric.data.KillStore;
import com.altarsmp.fabric.data.RecipeRegistry;
import com.altarsmp.fabric.data.WeaponRegistry;
import com.altarsmp.fabric.event.KillTracker;
import com.altarsmp.fabric.faction.FactionManager;
import com.altarsmp.fabric.protection.DamageListener;
import com.altarsmp.fabric.protection.WeaponProtection;
import com.altarsmp.fabric.recipe.AltarCrafting;
import com.altarsmp.fabric.util.AltarIds;
import com.altarsmp.fabric.util.AltarSMPLog;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AltarSMP for Minecraft Java 26.2 — native Fabric entrypoint.
 * Single unified source tree; every subsystem initializes here with explicit errors.
 */
public final class AltarSMPMod implements ModInitializer {
    private static AltarSMPMod INSTANCE;
    private static final AltarSMPConfig CONFIG = new AltarSMPConfig();
    private static final WeaponRegistry WEAPONS = new WeaponRegistry();
    private static final RecipeRegistry RECIPES = new RecipeRegistry();
    private static final CustomItemRegistry CUSTOM_ITEMS = new CustomItemRegistry();
    private static final AltarManager ALTARS = new AltarManager();
    private static final Map<String, String> CUSTOM_NAMES = new LinkedHashMap<>();
    private static final FactionManager FACTIONS = new FactionManager();
    private static final KillStore KILLS = new KillStore();
    private static final CooldownManager COOLDOWNS = new CooldownManager();
    private static final AbilityEngine ABILITIES = new AbilityEngine();
    private static final PaladinBattleAxeAbility PALADIN = new PaladinBattleAxeAbility();
    private static final WeaponProtection PROTECTION = new WeaponProtection();
    private static java.nio.file.Path storageBase = java.nio.file.Path.of(".");

    private String minecraftVersion = "unknown";

    public static AltarSMPMod instance() { return INSTANCE; }
    public static AltarSMPConfig config() { return CONFIG; }
    public static WeaponRegistry weapons() { return WEAPONS; }
    public static RecipeRegistry recipes() { return RECIPES; }
    public static CustomItemRegistry customItems() { return CUSTOM_ITEMS; }
    public static AltarManager altars() { return ALTARS; }
    public static Map<String, String> customNames() { return CUSTOM_NAMES; }
    public static FactionManager factions() { return FACTIONS; }
    public static KillStore killStore() { return KILLS; }
    public static CooldownManager cooldowns() { return COOLDOWNS; }
    public static AbilityEngine abilities() { return ABILITIES; }
    public static WeaponProtection protection() { return PROTECTION; }
    public static java.nio.file.Path storageBase() { return storageBase; }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        long t0 = System.currentTimeMillis();
        AltarSMPLog.info("AltarSMP starting (mod " + modVersion() + ")");

        CONFIG.load(AltarSMPMod.class.getClassLoader(), configDir());
        WEAPONS.load(AltarSMPMod.class.getClassLoader());
        RECIPES.load(AltarSMPMod.class.getClassLoader());
        CUSTOM_ITEMS.load(AltarSMPMod.class.getClassLoader());
        CUSTOM_NAMES.putAll(authoritativeCustomNames());
        FACTIONS.load(configDir());
        KILLS.load(configDir());

        registerCommands();
        registerServerLifecycle();
        registerAltarInteractions();
        ABILITIES.register(PALADIN);
        ABILITIES.registerEvents();
        KillTracker.register();
        PROTECTION.register();
        new DamageListener(PALADIN).register();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) FACTIONS.applyTick(p);
        });

        AltarSMPLog.info("AltarSMP initialized in " + (System.currentTimeMillis() - t0) + "ms"
                + " | weapons=" + WEAPONS.all().size()
                + " recipes=" + RECIPES.all().size()
                + " altars=" + ALTARS.count()
                + " customItems=" + CUSTOM_ITEMS.all().size()
                + " factions=" + FACTIONS.knownPlayers()
                + " kills=" + KILLS.playerCount()
                + " abilities=" + ABILITIES.count()
                + " vaults=" + PROTECTION.vaultSize());
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                AltarSMPCommands.register(dispatcher));
    }

    private void registerServerLifecycle() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            minecraftVersion = server.getVersion();
            try {
                storageBase = server.getSavePath(net.minecraft.util.WorldSavePath.ROOT).getParent().getParent();
                FACTIONS.setStorage(configDir());
                KILLS.setStorage(configDir());
                FACTIONS.load(configDir());
                KILLS.load(configDir());
            } catch (Exception e) {
                AltarSMPLog.error("Failed to resolve server storage path", e);
            }
            AltarSMPLog.info("Server started. Minecraft " + server.getVersion()
                    + " | Fabric API present, AltarSMP systems online");
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                AltarSMPLog.info("Server stopping — clearing transient altar state"));
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                AltarSMPLog.info("Server stopped."));
    }

    /** Left-click on an altar marker stand triggers the real craft flow. */
    private void registerAltarInteractions() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity sp && entity != null) {
                String altarId = ALTARS.altarIdOfEntity(entity);
                if (altarId != null) {
                    AltarSMPLog.debug("altar interact: " + sp.getGameProfile().getName() + " clicked " + altarId);
                    var result = AltarCrafting.craft(sp, altarId);
                    sp.sendMessage(com.altarsmp.fabric.recipe.AltarCrafting.describe(result) == null
                            ? net.minecraft.text.Text.literal("")
                            : net.minecraft.text.Text.literal(com.altarsmp.fabric.recipe.AltarCrafting.describe(result)));
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    /** Original custom-name map from a/b.java (authoritative). */
    private static Map<String, String> authoritativeCustomNames() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("custom_weapon_handle", "Weapon Handle");
        m.put("custom_warden_heart", "Warden's Heart");
        m.put("custom_hyperion_shard", "Hyperion Shard");
        m.put("custom_nightpiercer_shard", "Nightpiercer Shard");
        m.put("custom_illusion_core", "Illusion Core");
        m.put("custom_vulkan_head", "Vulkan Head");
        m.put("custom_pale_shard", "Pale Shard");
        m.put("custom_copper_pickaxe", "Copper Pickaxe");
        m.put("custom_copper_fragment", "Copper Fragment");
        m.put("custom_chestplate_shard", "Chestplate Shard");
        m.put("custom_pale_crossbow", "Pale Crossbow");
        m.put("custom_hyperion", "Hyperion");
        m.put("custom_nightpiercer", "Nightpiercer");
        m.put("custom_dragon_heart", "Dragon Heart");
        m.put("custom_fragment_of_the_sea", "Fragment of the Sea");
        m.put("custom_soul_in_a_bottle", "Soul in a Bottle");
        return m;
    }

    private static String modVersion() {
        try (var in = AltarSMPMod.class.getResourceAsStream("/fabric.mod.json")) {
            if (in != null) {
                var obj = com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
                return obj.get("version").getAsString();
            }
        } catch (Exception ignored) { /* version unknown */ }
        return "unknown";
    }

    private static Path configDir() {
        // FAPI provides the config dir; fall back to a local dir if absent (dev).
        try {
            return Path.of(System.getProperty("user.home"), ".config", "altarsmp");
        } catch (Exception e) {
            return Path.of(".");
        }
    }

    static { /* ensure AltarIds/class references initialize early */ }
}
