package com.mojang.brigadier.context;
public class CommandContext<S> {
    private final S source;
    public CommandContext(S source){this.source=source;}
    public S getSource(){return source;}
}
