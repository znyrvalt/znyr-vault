package com.mojang.brigadier.suggestion;
import java.util.concurrent.CompletableFuture;
public class SuggestionsBuilder {
    public SuggestionsBuilder suggest(String text){return this;}
    public CompletableFuture<Suggestions> buildFuture(){return CompletableFuture.completedFuture(new SuggestionsBuilder().build());}
    public Suggestions build(){return new Suggestions(java.util.List.of());}
}
