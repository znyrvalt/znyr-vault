package com.mojang.brigadier.builder;
import com.mojang.brigadier.tree.LiteralCommandNode;
public class LiteralArgumentBuilder<S> extends ArgumentBuilder<S,LiteralArgumentBuilder<S>> {
    private final String literal;
    protected LiteralArgumentBuilder(String literal){this.literal=literal;}
    public static <S> LiteralArgumentBuilder<S> literal(String name){return new LiteralArgumentBuilder<>(name);}
    public String getLiteral(){return literal;}
    public LiteralCommandNode<S> build(){return new LiteralCommandNode<>(literal);}
}
