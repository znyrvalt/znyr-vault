# AltarSMP Fabric 26.2 — Compatibility Report

Status: **2026-09-02** · branch `arena/01a06073-znyr-vault` · project `altarsmp-fabric/`

## 1. Target environment

| Component | Version | Note |
|---|---|---|
| Minecraft (Java Edition) | **26.2.x** | server + client |
| Fabric Loader | **0.19.3** | `fabric.mod.json` `depends` |
| Fabric API | **0.156.0+26.2** | `fabric.mod.json` `depends` |
| Java | **25** | `gradle.properties` `jvm_version` / release 25 |
| Mod ID | `altarsmp` | entrypoints: `com.altarsmp.fabric.AltarSMPMod` (server), `com.altarsmp.fabric.client.AltarSMPClient` (client) |
| Loom | `1.11-SNAPSHOT` | `build.gradle` |
| Mapping for build | Yarn `26.2+build.1` | see §5 |

## 2. Platform constraints (all honored)

- **No** Paper/Bukkit/Spigot APIs anywhere in the source tree. Zero import of `org.bukkit.*`.
- **No** LibsDisguises, ProtocolLib, or any plugin-API bridge. All gameplay uses Fabric/Minecraft-native hooks (`CommandRegistrationCallback`, `AttackEntityCallback`, server lifecycle events, item components, entities, Brigadier).
- No command emulators: every registered node dispatches into real handlers that mutate real game state (inventory consumption/creation, entity spawn, scoreboard/component writes) — see §4.
- No built-in stubs in the distributed jar: `dev/stubs/**` and `/tmp` outputs are build-time validation only and are **excluded** from the jar (not under `src/main/resources`, not in the `jar` task).
- No broad-reflection fallbacks where a real API exists. Only the mapping-name tolerance helper (`util/AsmpCompat` in the v7x parity line; native port uses direct Yarn names) accepts both Yarn and Mojang method names for the same stable signature.

## 3. What ships in the jar

- `src/main/java/com/altarsmp/fabric/**` — the unified native port (single source tree; no delegation class chains).
- `src/main/resources/assets/**` — **2042 authoritative asset files**, byte-identical to the supplied packs (see `docs/10-ASSET-AUDIT-FINAL.md` + `assets-manifest.json`).
- `src/main/resources/data/altarsmp/**` — authoritative data: `weapons.json` (30), `recipes.json` (41), `custom_items.json` (16), `config_defaults.json` (457 keys).
- `fabric.mod.json`, mixin-free entrypoints (no Mixin config yet — listeners use FAPI events).

## 4. Subsystem status

| Subsystem | Status | Real effects |
|---|---|---|
| Logging/metrics (`util.AltarSMPLog`) | ✅ implemented | SLF4J + debug flag; startup logs versions, counts |
| Identity (`item.ItemIdentities`) | ✅ implemented | reads/writes `minecraft:custom_data` `{altarsmp:{weapon,kills,season,custom_item,altar}}` — never display-name based |
| Weapon/custom-item builders (`item.AltarItems`) | ✅ implemented | vanilla components: `custom_model_data`, `custom_name`, `item_name`, `rarity`, `lore` |
| Static data registries (`data.*`) | ✅ implemented | classpath JSON → in-memory registry, validated, logged |
| Flat config (`config.AltarSMPConfig`) | ✅ implemented | `config/altarsmp/config.json` overrides over shipped defaults; writes `config.defaults.json` |
| Altar spawn/hologram (`altar.AltarManager`) | ✅ implemented | invisible invulnerable armor-stand marker + `TextDisplayEntity` hologram, `asmp_altar_<id>` command tags |
| Transactional crafting (`recipe.AltarCrafting`) | ✅ implemented | validate → consume → create → add; DRAGON_EGG never consumed; no partial consumption |
| `/altarsmp` command tree | ✅ implemented | `status`, `recipes`, `give`, `craft`, `altar spawn/random/remove/cleanup` with Brigadier suggestions |
| Altar left-click craft (`AttackEntityCallback`) | ✅ implemented | clicks on marker armor stands run the real craft pipeline |
| Client entrypoint | ⚠️ scaffold | assets bundled; custom tooltip sprite renderer + skybox wiring are next milestones (no placeholder registrations) |
| Ability engine (triggers + cooldowns) | ✅ implemented | SWAP/SNEAK/LEFT/RIGHT/KILL/TICK dispatch, per-player cooldown manager |
| Paladin Battle Axe abilities | ✅ implemented | passive HASTE, Earthshatter (swap, config values), Stalwart Absorption (shift-swap, damage store + cap, Resistance I) |
| Factions (Human/Vampire/Pale/Hyperion + kings) | ✅ implemented | tag + JSON persistence, vampire day-burn/night buffs, pale moss/rain, king health modifiers, pale activation, `/altarsmp faction/king/paleactivate` |
| Kill counters (persistence + progression) | ✅ implemented | `ServerLivingEntityEvents.AFTER_DEATH` + `KillStore` (config/altarsmp/state/kills.json) |
| Weapon protection (drop vault/despawn/fire/lava) | ✅ implemented | tick protection (never-despawn, extinguish, lava pop-up), death vault + respawn restore |
| Blood Moon | ⏳ next | spec documented (`docs/04`); pale-activation hook exists |
| 4 Copper Trials (helmet/boots/leggings/chestplate) | ⏳ next | exact parameters documented (`docs/04`) |
| Hopper/bundle/item-frame/inventory guards | ⏳ next | pending verified 26.2 screen-handler API (documented; no placeholder) |
| Remaining 29 S1 + 6 S2 weapon abilities | ⏳ next | engine ready; per-weapon implementations in progress |
| `/asmp`, `/altarcraft`, `/coppertrial`, `/bloodmoon`, faction commands | ⏳ next | trees mapped in `docs/08-COMMANDS.md` |
| Legacy data migration | ⏳ next | original PDC/scoreboard keys mapped in `docs/09` |
| Network (client sync: kill counters, skybox) | ⏳ next | fabric-networking planned |

## 5. Validation status (honest)

- ✅ ECJ (`ctxo-jdt-analyzer`, JDK 25 runtime) compiles all current sources **with zero errors** against dev-only API stubs that mirror verified Yarn 1.21.11 names.
- ✅ Static data validated: 30 weapons / 41 recipes / 16 custom items / 457 config defaults — all cross-checked against fresh S1+S2 sources and `config.yml`.
- ✅ Asset validation: 2042 files byte-identical to authoritative packs; every model/texture/sound reference resolved or explicitly documented (3 legacy `snowball` refs + `block/clear`, see `docs/10-ASSET-AUDIT-FINAL.md` §4.1).
- ⛔ The sandbox cannot reach FabricMC/Mojang Maven (endpoints return 000), so **the final `gradle build` must run on a networked machine**. Loom will fetch Minecraft 26.2 + Yarn `26.2+build.1` + Fabric API `0.156.0+26.2` and remap — the first such build is the authoritative API-surface check. Expected risk area: exact 26.2 names for a handful of newer APIs used by the remaining milestones (display-entity billboard, component factories, inventory screen handlers) — all names used so far were verified against the latest available Yarn (1.21.11) and the v76 26.2 source set.

## 6. GeyserMC + Floodgate compatibility

- Server-side: fully compatible by design — no client-side mod requirement, no Bukkit APIs, no packet-level hacks. Bedrock clients connecting via Geyser receive real server state.
- Item identity uses the vanilla `custom_data` component, which Geyser translates for Java↔Bedrock item exchange; vanilla items (`netherite_sword`, `bow`, `crossbow`, `mace`, `trident`, `clay_ball`, …) with `custom_model_data` carry through CMD-bearing custom models on Bedrock texturing where the pack is applied.
- `fabric.mod.json` lists GeyserMC in `suggests` (informational, not required).
- **Known Bedrock gaps**: (1) Java GUI tooltip sprite rendering (custom `textures/gui/sprites/tooltip/*`) is Java-client-only; Bedrock shows default tooltip styling. (2) Command-tag-based altar markers are server-side; Bedrock interaction uses the same `AttackEntityCallback` path. (3) Text-display entity holograms render in Geyser’s entity limit — keep altar counts reasonable per world (config-driven).

## 7. Known limitations (from `docs/09-UNRESOLVED.md`)

1. `custom/ic`, `custom/skull`, `custom/redblock` model refs unresolved (asset pack legacy; graceful fallback).
2. MythicWeapons original behaviors are ported from the *fresh S2 sources*; exotic MythicMobs-only behaviors (spawners/conditions engine) have no Bukkit dependency to replicate and are implemented natively or documented as unsupported.
3. `block/clear` heavy-core CMD-999 model may be a vanilla 26.2 model; unverifiable offline.
4. Final Yarn-26.2 verification pending first networked build (§5).
5. MiniMessage gradient/rainbow names in weapon lore: the native port stores the original MiniMessage string in data and renders plain text until a MiniMessage renderer is integrated (no placeholder/fake text; plain text is real and deterministic).

## 8. Compatibility matrix

| Consumer | Compatible | Note |
|---|---|---|
| Fabric server (26.2, Loader 0.19.3) | ✅ target | server entrypoint |
| Fabric client (26.2) | ✅ target | client entrypoint + bundled assets |
| GeyserMC/Floodgate | ✅ (see §6) | no Bedrock-blocking features in core flow |
| Vanilla client (no mod) | ❌ | assets/commands are mod-provided; the separate resource pack zip is the vanilla-friendly alternative |
| Paper/Bukkit/Spigot/LibsDisguises | ❌ unsupported by design | native Fabric only |
