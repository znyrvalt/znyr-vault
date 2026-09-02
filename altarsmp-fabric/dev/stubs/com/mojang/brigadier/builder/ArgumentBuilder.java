package com.mojang.brigadier.builder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
public abstract class ArgumentBuilder<S, T extends ArgumentBuilder<S,T>> {
    public T then(ArgumentBuilder<S,?> child){ return (T) this; }
    public T executes(Command<S> command){ return (T) this; }
    public T suggests(SuggestionProvider<S> provider){ return (T) this; }
    public T requires(java.util.function.Predicate<S> p){ return (T) this; }
    public abstract CommandNode<S> build();
}
