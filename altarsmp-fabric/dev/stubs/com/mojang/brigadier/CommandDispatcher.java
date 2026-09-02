package com.mojang.brigadier;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
public class CommandDispatcher<S> {
    public LiteralCommandNode<S> register(LiteralArgumentBuilder<S> builder){ return new LiteralCommandNode<S>(builder.getLiteral()); }
}
