# Trials & events (audited from source)

## Copper Helmet Trial — `events/CopperTrialEvent` + `items/CopperFragment`
- `/coppertrial helmet start` — 5 fragments (`custom_copper_fragment`, drop chance `trials.helmet.fragment-drop-chance=0.1`) spawn as real items; holders glow; `total cleared = 5/5` → completion, broadcast.
- Fragment collection tracked per trial run; fragment holders visible to all players.

## Copper Boots Bingo — `events/BingoEvent`
- `/bingo start|stop|status|tasks`; 1-hour (3600000 ms) duration; boss bar + sidebar.
- 40 tasks (exact, see below); per-player `Map<UUID,int[]>` progress + completed set; GUI (54-slot) with glass borders (slots 10..43) showing task completion; book item (`bingo_book` PDC) right-click opens GUI; completion detected on inventory change / pickup.
- Task board (all 40 verified from source):
  8 Diamonds, 16 Gold Ingots, 32 Iron Ingots, 64 Coal, 16 Lapis, 8 Emeralds, 16 Redstone, 4 Ancient Debris, 1 Netherite Ingot, 8 Blaze Rods, 16 Ender Pearls, 4 Ghast Tears, 8 Magma Cream, 16 Slime Balls, 4 Wither Skeleton Skulls, 1 Beacon, 16 Quartz Blocks, 8 End Crystals, 16 Phantom Membrane, 8 Nautilus Shells, 4 Heart of the Sea, 32 Amethyst Shards, 16 Copper Ingots, 8 Echo Shards, 4 Disc Fragments, 16 Glow Ink Sacs, 8 Honey Bottles, 16 Honeycomb, 4 Totem of Undying, 8 Golden Apples, 1 Enchanted Golden Apple, 4 Nether Stars, 16 Prismarine Shards, 8 Prismarine Crystals, 4 Tridents, 8 Saddles, 4 Name Tags, 8 Brewing Stands, 32 Glowstone, 16 Sea Lanterns.

## Copper Leggings Hot Potato — `events/HotPotatoEvent`
- `/hotpotato start|stop|status`; 45-minute duration; random starting holder; potato is a real held item with PDC; transfers on melee hit (`onPlayerHit`), drop blocked (`onPlayerDrop`), quit preserves map state; per-holder hold time accumulated; scoreboard shows remaining/leaders; winner = longest total hold on end.

## Copper Chestplate Shard Trial — `trials/CopperChestplateTrial`
- `/coppertrial chestplate start` (near player loc): builds island from `CUT_COPPER`/`CUT_COPPER_STAIRS`, `LIGHTNING_ROD` center (radius 2.5..30 as in source), spawns shards (`custom_chestplate_shard`).
- Constants: hazard radius 30 (`e`), shard count `f=60`…, lightning damage 20.0 (`g`), strike cooldown 3000ms (`h`), shard spawn interval 2.5s (`i`); `strikeLightning` = effect lightning + damage 20 + `ELECTRIC_SPARK` 30 particles + upward launch; **elytra glide blocked** (`onElytraGlide`) and **riptide blocked** (`onRiptide`) within radius; pickup only in trial scope; active-shard glow task; `isNearActiveTrial` check for all restrictions.

## Blood Moon — `managers/BloodMoonManager`
- `/bloodmoon [start|stop|status]`; on start: world time → 13000, duration loop (task), broadcasts; applies `bloodmoon_vampire`/`bloodmoon_pale` tags (players store `bloodmoon_pale` tag), visual/sky change, ends automatically; `shutdown()` cleanup.

## Deathmatch — `sessions/Deathmatch` (+ `commands/DeathmatchCommand`)
- Session-based arena: `/deathmatch ...`; teleports, clears, last-man-standing loop; separate from main systems.

## Other stateful systems
- `managers/ContagionSignalManager` — contagion/`/contagion stop`; `systems/BanZoneSystem` (`/banzone`, `/nukezone`); `utils/TrustManager` (`/trust`); `a/f.java` and `a/l.java` = fragment cooldown (3600s head drop, 40s…?) and per-player state; `a/k.java` = deathmatch-ish random rotation (120..80, 36000ms); `testworld/*` = dev test world (admin-only; ported as `testworld` package, not part of normal gameplay).
