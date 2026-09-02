package net.minecraft.server.command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
public class CommandManager extends CommandDispatcher<ServerCommandSource> {
    public static LiteralArgumentBuilder<ServerCommandSource> literal(String name){
        return LiteralArgumentBuilder.literal(name);
    }
    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type){
        return RequiredArgumentBuilder.argument(name, type);
    }
}
