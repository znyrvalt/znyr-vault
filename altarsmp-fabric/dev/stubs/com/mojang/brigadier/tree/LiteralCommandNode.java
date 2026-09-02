package com.mojang.brigadier.tree;
public class LiteralCommandNode<S> extends CommandNode<S> {
    private final String literal;
    public LiteralCommandNode(String literal){this.literal=literal;}
    public String getLiteral(){return literal;}
}
