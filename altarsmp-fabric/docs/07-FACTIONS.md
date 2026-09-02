# Faction system (audited from `vampire/*` + `AltarSMP`)

State = scoreboard tags `vampire`, `pale`, `human`, `hyperion` + `hyperion_blessed` + king tags; persistent store (plugin's `factions.yml`-equivalent → Fabric `config/altarsmp/state/factions.json` per-player).

| faction | tags | effects (tick 20) | health bonus | king health | special |
|---|---|---|---|---|---|
| Vampire | `vampire` | night (`time 0..12300` = day!): if daylight + sky light ≥15 → setFireTicks(40); at night (`!day`): STRENGTH lvl `vampire.night_strength_level`(0), SPEED lvl `night_speed_level`(1), FIRE_RESIST (if `night_fire_resistance`), all 40 ticks | `vampire_health` = 4.0 hearts | `vampire_king_health` = 10.0 | kill converts (tag `paleaffect`-style flows) |
| Pale | `pale` | speed lvl `pale.moss_speed_level`(1) when standing on MOSS block; rain (storm + sky light > 0, exposed): WEAKNESS lvl `rain_weakness_level`(1) | `pale_health` = 2.0 | `pale_king_health` = 5.0 | backstab ×`pale.backstab_multiplier`(1.4); `paleaffect` tag converts on pale-system activation |
| Hyperion | `hyperion` | combat modifiers vs vampire/pale (config `hyperion_bonus`=2.0 dmg bonus), purification abilities | — | — | crafts gated by faction (`curses.gate_faction_crafts`) |
| Human | `human` | default; immune to curses unless `allow_perma_human` | — | — | cursecraft only if `allow_perma` |

- `curses.enabled` gates the whole tick loop; `curses.allow_perma_vampire/pale/human` gates `/perma`.
- VampireManager core: `applyVampireEffects`, `applyPaleEffects`, `updateKingHealth` (AttributeModifier UUIDs fixed per faction), `activatePaleSystem`, `isPaleActivated`.
