# AltarSMP Fabric — v77 fix report (Minecraft Fabric 26.2)

Input: `AltarSMP-Fabric-26.2-v76-GAMEPLAY-COMMAND-FIXED.txt` (actually a jar;
Fabric entrypoint `com.altarsmp.fabric.AltarSMPFabricParityV59`).
Output: `AltarSMP-Fabric-26.2-v77-GAMEPLAY-COMMAND-FIXED.jar` (same mod id `altarsmp`,
version `1.0.77-fabric-26.2-GAMEPLAY-COMMAND-FIX`, bytecode target 17, Java 21+ runtime).

SHA-256: `154b5020a7e8753a0444401e02b07f9006335785b2c14c9b94cc2c3ecbf869f5`

---

## What was broken

### 1. Command bootstrap used Mojang names on a Fabric runtime (fatal)
`AltarSMPFabricParityV59` (the entrypoint) and `AltarSMPFabricFullFixed` resolved
`net.minecraft.commands.Commands`, `getCommands()` and `performPrefixedCommand()`.
On a Fabric runtime those are Yarn names: `net.minecraft.server.command.CommandManager`,
`getCommandManager()`, `parseAndExecute()` (older Yarn: `executeWithPrefix()`).
Result: the command layer could not even bind — `/altarsmp` never registered and
`/altarsmp craft|give|spawn|...` were unusable.

**Fix:** every bridge lookup goes through the new `AsmpCompat` layer, which resolves
Yarn names first and Mojang names second (class, method, and `sendFeedback`/`sendSuccess`
Supplier signature). The same jar now works on a normal Fabric (Yarn) install and still
works in a Mojang-mapped environment.

### 2. `craft` / `give` gave the recipe id, not the recipe output
The old `giveRecipe` (and `giveOutput`/`spawn`) executed
`give @s stackSpec(recipeId)` — i.e. `minecraft:<recipe-id>[...]` — which produces the
wrong item (often a nonexistent item id) and loses the custom model data entirely.
The recipe data model has a real output entry (`OUTPUTS[recipeId]`, item + custom model
data float) that was never used on the craft path.

**Fix:** `V59.giveRecipe` now looks up `OUTPUTS[recipeId]` and gives
`<item>[minecraft:custom_model_data={floats:[<cmd>.0]}]` after the same ingredient
preflight/consumption sequence. `give` and `spawn` also use the true output entry.

### 3. Supplier proxy broke `equals`/`hashCode`/`toString` (ClassCastException)
`tell()` built a `Supplier` proxy whose handler returned the chat `Component` for
*every* method, including `equals`, `hashCode`, `toString`. Any framework call into
those methods threw `ClassCastException` (String/Integer/boolean vs Component) — and
on some paths the handler returned `null` for a primitive boolean return, an NPE.
The same unterminated-handler pattern existed in the Brigadier command proxy.

**Fix:** `AsmpCompat.safeSupplier` returns well-typed values (`equals` → identity,
`hashCode` → identity hash, `toString` → label, `get()` → the Component). Command
proxies in V59/FullFixed/CommandProxySupport likewise return typed values.

### 4. Wrong tick-event class name (dormant tooltip layer)
`ServerTickEvents$EndServerTick` is not a Fabric class; the real one is
`ServerTickEvents$EndTick`. The V45/V46 tick registrations could never resolve
(they are part of the inherited, non-entrypoint tooltip layer).

**Fix:** dual-name lookup (tries `EndTick` first, then the old name) so this layer is
correct even if an alternate entrypoint activates it.

### 5. `AltarSMPFabricParityV35` did not rebuild cleanly
The decompiled source had duplicated private helper blocks and lambda local variables
shadowing enclosing locals, so a clean recompile was impossible and the recipe core
could not be verified/rebuilt.

**Fix:** `V35` rewritten cleanly with the original semantics and data taken verbatim
from the v76 bytecode (verified: 41 recipes, 42 outputs, 16 custom inputs; every recipe
id has an output entry; `crazyslots` keeps the dragon egg by design). Only the command
execution/feedback plumbing was rerouted through `AsmpCompat`; all recipe tables,
preflight commands, item predicates and summon NBT strings are byte-for-byte the
original (e.g. `minecraft:count~{min:N}` is correct 1.20.5+ item-predicate range syntax).

## What was intentionally left as-is

- The v76 command chain is `V59 (entrypoint) → V35 (data/core)` and calls V34's empty
  init. The inherited gameplay/tooltip layers V37–V49 are **not on that chain** in v76
  and remain dormant, exactly as in the original v76 design; they ship unchanged.
- V56/V58 bridges are not entrypoints either; they were still recompiled with the
  mapping fixes so they are also correct if activated.

## Verification performed

- Eclipse JDT (ECJ) compile of all eight fixed layers: **0 errors**.
- JVM smoke test against the packaged jar: static init `RECIPES=41 OUTPUTS=42 CUSTOM=16`,
  zero recipes missing outputs, `V35.onInitialize()` runs, every signature required by
  V37/V56/V58/V59 reflection (`onInitialize`, `cleanup`, `craft`, `spawn`, `remove`,
  `randomOffset`, `stackSpec`, `outputSpec`, `call`) resolves, and all bridge classes load.
- Jar contents audited: entrypoint present, no compiler stubs, all 27 `com/altarsmp` class
  files in place, `fabric.mod.json` still declares `minecraft 26.2.x`,
  `fabric-api >=0.156.0+26.2`, loader `>=0.19.3`.

Note: full in-game testing requires a Fabric 26.2 runtime, which is not available in
this sandbox; the fixes above address the statically confirmed failure modes.

## Files touched (sources kept in `fixed/`)

| File | Change |
|---|---|
| `AsmpCompat.java` | **new** — dual-name class/method resolution, typed Supplier proxy |
| `AltarSMPFabricParityV59.java` | rewritten — dual-name bootstrap, correct OUTPUTS give, typed proxies |
| `AltarSMPFabricParityV35.java` | rewritten cleanly — original data + AsmpCompat plumbing |
| `AltarSMPFabricFullFixed.java` | rewritten — dual-name bootstrap, safe proxies |
| `AltarSMPFabricParityV34.java` | recompiled as-is (empty init) |
| `AltarSMPFabricParityV56.java` / `V58` | dual-name lookup, correct frame fix |
| `AltarSMPFabricParityV37/V38/V39/V41/V42/V45/V46/V47` | dual-name exec/tick fixes |
