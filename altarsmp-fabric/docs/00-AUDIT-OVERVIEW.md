# AltarSMP → Fabric 26.2 — Authoritative inventory (audit #1)

Source material (all on branch `arena/01a06073-znyr-vault`, commit `75aed8b`):

| file | entries | content |
|---|---|---|
| `Altar_SMPS1-2-sources-FRESH.jar` | 290 (258 .java) | full plugin source: `com.altarsmp` (S1, 182 files), `com.altarsmps2` (S2, 46 files), `a/` obfuscation-helper pkg (30) |
| `Altar_SMPS1-2 (1).jar` | 542 (538 .class) | compiled plugin (v1.2, api 1.21) + authoritative `config.yml`, `plugin.yml` |
| `AltarSMP-ResourcePack.zip` | 2112 | main resource pack: mythicweapons 1475, minecraft 344, altarsmp 249, custom 26, skyboxengine 11, altarsmps2 5 |
| `AltarSMP.zip` | 246 | legacy/secondary pack: minecraft 211, custom 32 |

## What the original really is
A **Bukkit 1.21 plugin** (`com.altarsmp.AltarSMP`, version 1.2) that:
- defines ~30 "AltarSMP" legendary weapons/items itself (S1+S2, classes per weapon, PDC identity + custom model data),
- implements the gameplay loop via **armor-stand hologram altars** (not blocks): left-click the named armor stand next to the hologram → recipe check → consume → give weapon,
- drives recipes from **config.yml** (`recipes.<id>`), with custom-item display-name matching,
- has factions (Human/Vampire/Pale/Hyperion + King states) via scoreboard tags `vampire`/`pale`/`human`/`hyperion`, persistent in a `factions.yml`-style store,
- has 4 copper trials (helmet fragment hunt, boots bingo, leggings hot potato, chestplate shard island),
- Blood Moon (`/bloodmoon`), deathmatch, ban-zone/nuke-zone, trust system, test world, contagion signal,
- **soft-depends on LibsDisguises** (morphs) and **MythicWeapons** (shared weapon detection + altar integration + the big resource pack),
- ships `plugin.yml` (~60 admin commands), `config.yml` (872 keys), resource pack with 49 tooltip-sprite families and 546 mythicweapons item/fx models.

## The audit files
- `01-WEAPONS.md` — 30 weapons (S1+S2): id, name, base material, CMD, source class.
- `02-RECIPES.md` — 41 altar recipes + 16 custom item identities.
- `03-CONFIG.md` — full authoritative config tree (872 keys) + behavior notes.
- `04-TRIALS-EVENTS.md` — 4 copper trials, bingo 40-task board, blood moon, deathmatch, event lifecycle, max-health/effect behaviors.
- `05-ASSETS.md` — resource-pack namespace/type inventory, pack metadata.
- `06-PROTECTION.md` — weapon protection / storage prevention / PvP / pale-effect listeners.
- `07-FACTIONS.md` — vampire/pale/human/hyperion behaviors, kings, curses.
- `08-COMMANDS.md` — every command surface (Bukkit + S2) mapped to Fabric equivalents.
- `09-UNRESOLVED.md` — gaps: MythicWeapons plugin behaviors, LibsDisguises morphs, legacy refs.

## Key architecture decisions for the port
1. **One unified source tree** `com.altarsmp.fabric` + `com.altarsmps2.fabric`, no version-delegate chains.
2. **Identity = vanilla components** (`minecraft:custom_data` with `altarsmp:{weapon:"<id>",kills:N}` + `custom_model_data` float), not display names. Display names kept for tooltips/Geyser.
3. **Altars in Fabric = armor-stand holograms** (same as original): `ArmorStandEntity` + `TextDisplayEntity` above it; `AttackEntityCallback` triggers crafting; keep the `altarsmp:altar_<id>` tags for cleanup.
4. **Recipes/data from `config.yml` compiled to JSON** (`data/altarsmp/*.json`) so the whole table ports 1:1 and stays configurable at runtime via a JSON config (auto-migrated from the original section names).
5. **Client**: resource-pack assets go into the jar under their original namespaces; custom-model textures/models used directly by vanilla item rendering; skybox engine limited.
6. **No stubs shipped**: Fabric/Minecraft APIs are real; the released jar contains no mod-provided `net.minecraft`/`com.mojang`/`net.fabricmc` classes.
