# AltarSMP Fabric — FINAL ASSET AUDIT

Generated **2026-09-02** from the authoritative resource packs (`/home/user/auth/respack` + `/home/user/auth/altarsmp`) and the previous v76 asset tree. Machine-readable manifest: [`assets-manifest.json`](../assets-manifest.json) (SHA-256 per file).

## 1. Merge result

| Metric | Value |
|---|---|
| Files merged into `src/main/resources/assets/` | **2042** |
| Copied byte-identical from authoritative packs | 2042 (100 %) |
| Modified from source | 0 |
| Generated (AI/redrawn) | 0 |
| Origin `respack` (AltarSMP + MythicWeapons S1/S2 pack) | 1980 |
| Origin `altarsmp` (secondary pack) | 62 |

> Per standing constraint, **no asset was redrawn or substituted**. Every file in the merged tree is byte-identical to an authoritative source file; the 62 `altarsmp`-origin files are the S1 copper-armor pack pieces that the main pack did not include.

## 2. Files by namespace

| Namespace | Files | Content |
|---|---|---|
| `altarsmp` | 192 | GUI tooltip sprites (49 families: amaranth, ancient_blade, arbiter, biome_blade, blaze_bringer, bloodlust, bone_blade, …) + `textures/gui` + misc |
| `mythicweapons` | 1453 | item definitions (547), models (weapons 86, vfx 325, item, block), textures (item 197, weapons 120, …), font, sounds |
| `minecraft` | 371 | item definitions (overrides: netherite_sword, bow, crossbow, mace…), models/weapons (79), block models (5), textures (tooltip sprites 57, hyperion, sounds assets) |
| `custom` | 20 | copper armor item defs + models (4+4), sounds.json + 5 `.ogg` |
| `altarsmps2` | 3 | S2 sounds (dragonding, dragontick_tack) |
| `skyboxengine` | 3 | skybox shader/model items |

## 3. Diff vs. v76 asset tree

The v76 distribution shipped the same pack structure (its `assets` tree holds **1980** files — the earlier “17,988” figure in session notes was wrong; `assets/minecraft/models` holds 79).

| Diff class | Count | Detail |
|---|---|---|
| v76 files **missing** from merged tree | **0** | merged set is a strict superset |
| Files added vs v76 | **62** | `custom/sounds.json` + 4 `custom/sounds/*.ogg` (blood-step/vampire sounds; 5 files) · `minecraft/textures/gui/sprites/tooltip/{ancient,bone,dragon,earth,frost,gold,green,omen,pure,purple,red,shard,tidebreaker,vulcan}_{background,frame}.png` (+14 `.mcmeta`; 56 files) · `minecraft/textures/item/hyperion.png.mcmeta` (1) |
| Files whose bytes differ from v76 | **4** | `custom/models/item/copper_{helmet,chestplate,leggings,boots}.json` — only the parent string differs: v76 `"minecraft:item/generated"` vs authoritative `"item/generated"`. Both resolve to `minecraft:item/generated`; authoritative version kept |

## 4. Reference validation (no silent gaps)

Validated every JSON reference in the merged tree against the tree itself; `minecraft:`-namespace references are accepted as client-supplied (vanilla) only where they name standard vanilla assets.

| Reference class | Resolved locally | Vanilla-supplied | Unresolved |
|---|---|---|---|
| Model `parent` (`.json`) | 0 | 27 | 0 |
| Model `textures` (`*.png`) | 1292 | 29 | 0 |
| Item-definition `model:` (`.json`) | 543 | 36 | 3 |
| Blockstate `model:` | 3 | 0 | 0 |
| Sound entries (`sounds.json`) | 13 | 11 | 0 |

### 4.1 Unresolved references (kept + documented, never deleted)

`minecraft/items/snowball.json` (shipped identically by both authoritative packs and v76) dispatches on `custom_model_data`; entries fall back to `minecraft:item/snowball` at CMD 0 when the CMD-specific model is absent — the item still renders.

| Ref | File | CMD | Resolution |
|---|---|---|---|
| `custom/ic` | `minecraft/items/snowball.json` | 1 | Not present in `respack`, `altarsmp`, or v76 trees; **no** reference in fresh S1/S2 Java or `config.yml`; documented in `docs/09-UNRESOLVED.md` |
| `custom/skull` | `minecraft/items/snowball.json` | 2 | same |
| `custom/redblock` | `minecraft/items/snowball.json` | 3 | same |
| `block/clear` | `minecraft/items/heavy_core.json` | 999 | Not in pack; may be a vanilla 26.2 model (unverifiable from sandbox — Maven/JAR access blocked). Fallback `block/heavy_core` resolves locally |

These are the **only** unresolved references in the tree; they are recorded here and in `docs/09-UNRESOLVED.md` rather than silently edited out of the authoritative files.

## 5. Explicit `custom_model_data` usage

Only three models in the packs carry an explicit `custom_model_data` float list (`docs/05-ASSETS.md`): `mythicweapons` dagger, dragonheart and vulcan skull (`1.0`). All other item identity/CMD is applied at runtime by the mod via the `custom_model_data` component (`item/AltarItems`), matching the original static `CustomModelData` NBT approach.

## 6. Packaging

- Mod jar: `src/main/resources/assets/**` ships inside the jar (namespace `altarsmp` + friends). No `pack.mcmeta` is needed inside the jar; Fabric resolves mod assets directly.
- Separate client resource-pack zip (release bundle): rebuilt from the same tree with `pack.mcmeta` (`pack_format` 75–199 per the authoritative `respack/pack.mcmeta`; `altarsmp/pack.mcmeta` (pack_format 47) retained for the historical pack deliverable) and `pack.png` from `auth/altarsmp`.
- `hyperion.png.mcmeta` is the authoritative animated-texture metadata; frame time 1 (matches v76 non-animated behavior plus original animation intent).

Checksums: every file listed in `assets-manifest.json`; the release manifest will aggregate jar/zip SHA-256 at build time.
