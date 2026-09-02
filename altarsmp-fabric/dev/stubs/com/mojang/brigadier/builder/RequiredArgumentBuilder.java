package com.mojang.brigadier.builder;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
public class RequiredArgumentBuilder<S,T> extends ArgumentBuilder<S,RequiredArgumentBuilder<S,T>> {
    private final String name;
    private final ArgumentType<T> type;
    protected RequiredArgumentBuilder(String name, ArgumentType<T> type){this.name=name;this.type=type;}
    public static <S,T> RequiredArgumentBuilder<S,T> argument(String name, ArgumentType<T> type){return new RequiredArgumentBuilder<>(name,type);}
    public ArgumentCommandNode<S,T> build(){return new ArgumentCommandNode<>(name);}
}
