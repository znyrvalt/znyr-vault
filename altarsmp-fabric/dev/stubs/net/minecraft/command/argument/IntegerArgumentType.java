package net.minecraft.command.argument;
import com.mojang.brigadier.context.CommandContext;
public class IntegerArgumentType implements com.mojang.brigadier.arguments.ArgumentType<Integer> {
    private IntegerArgumentType(){}
    public static IntegerArgumentType integer(int min,int max){ return new IntegerArgumentType(); }
    public static int getInteger(CommandContext<?> ctx, String name){ return 1; }
}
