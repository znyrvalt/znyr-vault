package net.minecraft.server;
public class MinecraftServer {
    public String getVersion(){return "26.2-stub";}
    public PlayerManager getPlayerManager(){return new PlayerManager();}
    public java.nio.file.Path getSavePath(net.minecraft.util.WorldSavePath path){return java.nio.file.Path.of(".");}
}
