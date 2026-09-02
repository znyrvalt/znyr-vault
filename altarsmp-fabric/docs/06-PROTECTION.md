# Protection & listener systems (audited)

| listener | purpose | Bukkit events used → Fabric equivalents |
|---|---|---|
| `AltarProtection` | block breaking/placing near altars, altar entity damage | `BlockBreakEvent/BlockPlaceEvent` → `PlayerBlockBreakEvents`/`PlayerPlaceBlockCallback`; entity attack → `AttackEntityCallback` |
| `WeaponStoragePrevention` | legendaries can't go into chest/barrel/ender-chest/hopper/shulker/bundle/other; burn protection | inventory click/move/drag + `InventoryClickEvent`-family → `InventoryScreen`, `ServerEntityInteract`/`PlayerInventory` hooks — closest Fabric: `InventoryManager`/`S2C`+`ServerPlayNetworking` validation or `ServerPlayerEntity` container sync interception |
| `PvpProtection` | PvP toggle (`/pvptoggle`), protection in safe zones | `EntityDamageByEntityEvent` → `AttackEntityCallback` |
| `PaleEffectProtection` | blocks applying glow/moss-removal to pale players | effect events → `LivingEntity` status-effect hooks + server tick |
| `TooltipStyleEnforcer` | enforces `altarsmp:tooltip/<key>` custom tooltip sprite via `minecraft:custom_model_data` + custom component | item tooltip render — client `TooltipComponent`/`ItemTooltipCallback` |
| `AltarBreakCleanup` | removing armor stands/holograms on altar break | `EntityRemoval`/`BlockBreak` → cleanup tags |
| `PlayerDeathLightning` | lightning + sound on legendary death | `EntityDeathCallback` |
| `PlayerHeadDrop` | head drop with 3600s cooldown | `PlayerDeathCallback` |
| `WardenHeartDrop`, `OminousVaultListener` | special drops/vault integration | `EntityDeathCallback`, `BlockBreak`/vault |
| S2: `AltarProtection`, `WeaponStoragePrevention`, `DragonHeartPlacePrevention`, `HappyGhastSaddleListener`, `EnderDragonTracker`, `EnderDragonNameListener`, `AmethystToolListener`, `CooldownMirror` | same + S2 specifics | same hooks |

Weapon identity validation is centralized (see `data/weapon identity` section in the port design): every protection rule resolves the weapon first via component `altarsmp/weapon_id` and only then applies.
