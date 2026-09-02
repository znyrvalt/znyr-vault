package com.mojang.brigadier.arguments;
public final class StringArgumentType implements ArgumentType<String> {
    private StringArgumentType(){}
    public static StringArgumentType word(){return new StringArgumentType();}
    public static String getString(com.mojang.brigadier.context.CommandContext<?> ctx, String name){return "x";}
}
