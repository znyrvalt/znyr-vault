package net.minecraft.command.argument;
import com.mojang.brigadier.context.CommandContext;
public class BoolArgumentType implements com.mojang.brigadier.arguments.ArgumentType<Boolean> {
    private BoolArgumentType(){}
    public static BoolArgumentType bool(){ return new BoolArgumentType(); }
    public static boolean getBool(CommandContext<?> ctx, String name){ return false; }
}
