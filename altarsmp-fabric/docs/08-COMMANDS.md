# Command surface (audited)

Bukkit commands in `plugin.yml` (main) + classes (all have permission `altarsmp.admin` unless noted):

`altarsmp`(give|reload), `altartooltip`, `lockaltars`, per-weapon give commands (bloodlust, boneblade, vulcanscrossbow, hyperion, wandofillusion, frostscythe, crazyslots, minorcrazyslots, nightpiercer, windweaver, witherbone, shadowblade, pureblade, earthgauntlet, paladinbattleaxe, cutlass, palecrossbow, contagionsignal, eclipsesword, knightfall, striker, nukelauncher, echo, fireslash), `ability`, `banzone`, `config`, `contagion stop`, `controls`, `cooldown`, `curse`, `deathmatch`, `destroyaltars`, `givepalelist`?, `legendaryconfig`, `lockaltars`, `morphlock`, `nukezone`, `permastatus`, `pvptoggle`, `setbloodlust`, `setkills`, `tabcolor`, `tooltipdebug`, `trust`, `bingo`, `hotpotato`, `coppertrial`, `bloodmoon`, `altar` (spawn/random/remove), `recipes`.

S2 (`com.altarsmps2.commands`): `ability`, `altar`, `blueparticle`, `controls`, `cooldown`, `legendaries2`, `legendaryconfig`, `recipes`, `setkills`, `trust`.

Fabric port consolidates into Brigadier trees (real implementations, real permission checks):
`/altarsmp` {status|recipes|give <weapon>|craft <recipe>|reload}, `/altarcraft <recipe>` (alias), `/asmp`, `/coppertrial {helmet|boots|leggings|chestplate} {start|stop|status}`, `/bloodmoon {start|stop|status}`, `/bingo {start|stop|status|tasks}`, `/hotpotato {start|stop|status}`, `/faction {vampire|pale|human|hyperion|set|status}`, `/weapons`, per-weapon `/give` aliases. All gone through `PermissionRegistry` (ops/fabric permission API) and real handlers.
