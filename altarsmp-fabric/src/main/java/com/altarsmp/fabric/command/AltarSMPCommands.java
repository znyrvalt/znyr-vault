package com.altarsmp.fabric.command;

import com.altarsmp.fabric.AltarSMPMod;
import com.altarsmp.fabric.data.WeaponDefinition;
import com.altarsmp.fabric.item.AltarItems;
import com.altarsmp.fabric.recipe.AltarCrafting;
import com.altarsmp.fabric.recipe.CraftingResult;
import com.altarsmp.fabric.util.AltarSMPLog;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** /altarsmp — real administrative interface over the real systems. */
public final class AltarSMPCommands {
    private AltarSMPCommands() {}

    public static void register(net.minecraft.server.command.CommandManager dispatcher) {
        dispatcher.register(literal("altarsmp")
                .requires(src -> src.hasPermissionLevel(2))
                .then(literal("status").executes(ctx -> status(ctx)))
                .then(literal("recipes").executes(ctx -> recipes(ctx)))
                .then(literal("give")
                        .then(argument("weapon", StringArgumentType.word())
                                .suggests(weaponSuggestions())
                                .executes(ctx -> give(ctx, StringArgumentType.getString(ctx, "weapon")))))
                .then(literal("craft")
                        .then(argument("recipe", StringArgumentType.word())
                                .suggests(recipeSuggestions())
                                .executes(ctx -> craft(ctx, StringArgumentType.getString(ctx, "recipe")))))
                .then(literal("faction")
                        .then(literal("get").executes(ctx -> factionGet(ctx)))
                        .then(literal("set")
                                .then(argument("player", StringArgumentType.word())
                                        .then(argument("faction", StringArgumentType.word())
                                                .suggests(factionSuggestions())
                                                .executes(ctx -> factionSet(ctx,
                                                        StringArgumentType.getString(ctx, "player"),
                                                        StringArgumentType.getString(ctx, "faction")))))))
                .then(literal("ability")
                        .then(argument("trigger", net.minecraft.command.argument.IntegerArgumentType.integer(1, 2))
                                .executes(ctx -> ability(ctx, net.minecraft.command.argument.IntegerArgumentType.getInteger(ctx, "trigger")))))
                .then(literal("altar")
                        .then(literal("spawn")
                                .then(argument("id", StringArgumentType.word())
                                        .suggests(weaponSuggestions())
                                        .executes(ctx -> spawn(ctx, StringArgumentType.getString(ctx, "id"), false))))
                        .then(literal("random")
                                .then(argument("id", StringArgumentType.word())
                                        .suggests(weaponSuggestions())
                                        .executes(ctx -> spawn(ctx, StringArgumentType.getString(ctx, "id"), true))))
                        .then(literal("remove")
                                .then(argument("id", StringArgumentType.word())
                                        .suggests(weaponSuggestions())
                                        .executes(ctx -> remove(ctx, StringArgumentType.getString(ctx, "id")))))
                        .then(literal("cleanup").executes(ctx -> cleanup(ctx))))
                .then(literal("paleactivate")
                        .then(argument("player", StringArgumentType.word())
                                .executes(ctx -> paleActivate(ctx, StringArgumentType.getString(ctx, "player")))))
                .then(literal("king")
                        .then(argument("player", StringArgumentType.word())
                                .then(argument("value", net.minecraft.command.argument.BoolArgumentType.bool())
                                        .executes(ctx -> king(ctx,
                                                StringArgumentType.getString(ctx, "player"),
                                                net.minecraft.command.argument.BoolArgumentType.getBool(ctx, "value")))))));
        AltarSMPLog.info("Commands registered: /altarsmp status|recipes|give|craft|altar spawn/random/remove|cleanup");
    }

    private static SuggestionProvider<ServerCommandSource> weaponSuggestions() {
        return (ctx, builder) -> {
            for (WeaponDefinition w : AltarSMPMod.weapons().all()) builder.suggest(w.id);
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<ServerCommandSource> recipeSuggestions() {
        return (ctx, builder) -> {
            for (com.altarsmp.fabric.data.AltarRecipe r : AltarSMPMod.recipes().all()) builder.suggest(r.id);
            return builder.buildFuture();
        };
    }

    private static int status(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        src.sendFeedback(() -> Text.literal("AltarSMP: weapons=" + AltarSMPMod.weapons().all().size()
                + " recipes=" + AltarSMPMod.recipes().all().size()
                + " altars=" + AltarSMPMod.altars().count()
                + " customItems=" + AltarSMPMod.customItems().all().size()), false);
        AltarSMPLog.debug("command /altarsmp status by " + src.getName());
        return 1;
    }

    private static int recipes(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        StringBuilder sb = new StringBuilder("AltarSMP recipes: ");
        for (com.altarsmp.fabric.data.AltarRecipe r : AltarSMPMod.recipes().all()) {
            sb.append(r.id).append(" ");
        }
        src.sendFeedback(() -> Text.literal(sb.toString().trim()), false);
        return 1;
    }

    private static int give(CommandContext<ServerCommandSource> ctx, String id) {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            src.sendFeedback(() -> Text.literal("Only players can receive weapons."), false);
            return 0;
        }
        WeaponDefinition w = AltarSMPMod.weapons().get(id);
        if (w == null) {
            src.sendFeedback(() -> Text.literal("Unknown weapon: " + id + " (see /altarsmp recipes)"), false);
            return 0;
        }
        try {
            var stack = AltarItems.createWeapon(w);
            int leftover = player.getInventory().addStack(stack);
            if (leftover > 0) player.dropItem(stack, false);
            src.sendFeedback(() -> Text.literal("Gave " + w.name + " to " + player.getGameProfile().getName()), false);
            AltarSMPLog.info("give: " + src.getName() + " -> " + player.getGameProfile().getName() + " weapon=" + w.id);
            return 1;
        } catch (Exception e) {
            AltarSMPLog.error("give failed for " + id, e);
            src.sendFeedback(() -> Text.literal("Give failed (see log): " + e.getMessage()), false);
            return 0;
        }
    }

    private static int craft(CommandContext<ServerCommandSource> ctx, String recipeId) {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) {
            src.sendFeedback(() -> Text.literal("Only players can craft at altars."), false);
            return 0;
        }
        CraftingResult result = AltarCrafting.craft(player, recipeId);
        src.sendFeedback(() -> Text.literal(AltarCrafting.describe(result)), false);
        AltarSMPLog.debug("command /altarsmp craft " + recipeId + " -> " + result.status());
        return result.status() == CraftingResult.Status.OK ? 1 : 0;
    }

    private static int spawn(CommandContext<ServerCommandSource> ctx, String id, boolean random) {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) return 0;
        int range = AltarSMPMod.config().getInt("altar-spawn.default-range", 500);
        double dx = 0, dz = 0;
        if (random) {
            double angle = Math.random() * Math.PI * 2;
            double dist = 24 + Math.random() * (range - 24);
            dx = Math.cos(angle) * dist;
            dz = Math.sin(angle) * dist;
        }
        var pos = player.getPos();
        AltarSMPMod.altars().spawn(player.getServerWorld(), pos.x + dx, pos.y, pos.z + dz, player.getYaw(), id, id);
        src.sendFeedback(() -> Text.literal("Altar spawned: " + id), false);
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> ctx, String id) {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) return 0;
        var pos = player.getPos();
        AltarSMPMod.altars().removeNear(player.getServerWorld(), pos.x, pos.y, pos.z, id, 64 * 64);
        src.sendFeedback(() -> Text.literal("Removed altars near you: " + id), false);
        return 1;
    }

    private static SuggestionProvider<ServerCommandSource> factionSuggestions() {
        return (ctx, builder) -> {
            for (com.altarsmp.fabric.faction.Faction f : com.altarsmp.fabric.faction.Faction.values()) builder.suggest(f.name().toLowerCase());
            return builder.buildFuture();
        };
    }

    private static int factionGet(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) return 0;
        var f = AltarSMPMod.factions().of(player);
        src.sendFeedback(() -> Text.literal("Your faction: " + f.name()
                + (AltarSMPMod.factions().isKing(player) ? " (king)" : "")
                + (AltarSMPMod.factions().isBlessed(player) ? " (hyperion blessed)" : "")), false);
        return 1;
    }

    private static int factionSet(CommandContext<ServerCommandSource> ctx, String playerName, String factionName) {
        ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(playerName);
        if (target == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("Player not online: " + playerName), false);
            return 0;
        }
        var f = com.altarsmp.fabric.faction.Faction.byName(factionName);
        if (f == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("Unknown faction: " + factionName), false);
            return 0;
        }
        AltarSMPMod.factions().setFaction(target, f);
        ctx.getSource().sendFeedback(() -> Text.literal(target.getGameProfile().getName() + " is now " + f.name()), false);
        return 1;
    }

    private static int ability(CommandContext<ServerCommandSource> ctx, int trigger) {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = src.getPlayer();
        if (player == null) return 0;
        boolean ok = AltarSMPMod.abilities().trigger(player, trigger);
        src.sendFeedback(() -> Text.literal(ok ? "Ability triggered." : "No AltarSMP weapon in hand."), false);
        return ok ? 1 : 0;
    }

    private static int paleActivate(CommandContext<ServerCommandSource> ctx, String playerName) {
        ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(playerName);
        if (target == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("Player not online: " + playerName), false);
            return 0;
        }
        AltarSMPMod.factions().activatePaleSystem(target);
        ctx.getSource().sendFeedback(() -> Text.literal("Pale system activated for " + target.getGameProfile().getName()), false);
        return 1;
    }

    private static int king(CommandContext<ServerCommandSource> ctx, String playerName, boolean value) {
        ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(playerName);
        if (target == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("Player not online: " + playerName), false);
            return 0;
        }
        AltarSMPMod.factions().setKing(target, value);
        ctx.getSource().sendFeedback(() -> Text.literal(target.getGameProfile().getName() + " king=" + value), false);
        return 1;
    }

    private static int cleanup(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = src.getPlayer();
        if (player != null) AltarSMPMod.altars().cleanupAll(player.getServerWorld());
        src.sendFeedback(() -> Text.literal("All altars cleaned."), false);
        return 1;
    }

    // unused local helpers removed; kept for API clarity
    @SuppressWarnings("unused")
    private static Supplier<Text> feedback(String s) { return () -> Text.literal(s); }
}
