package net.minecraft.world;
public class World {
    private final boolean client;
    public World(boolean client){this.client=client;}
    public boolean isClient(){return client;}
}
