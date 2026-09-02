package net.minecraft.server.command;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.function.Supplier;
public class ServerCommandSource {
    public void sendFeedback(Supplier<Text> text, boolean broadcast){}
    public ServerPlayerEntity getPlayer(){return null;}
    public String getName(){return "stub";}
    public boolean hasPermissionLevel(int level){return true;}
    public net.minecraft.server.MinecraftServer getServer(){return new net.minecraft.server.MinecraftServer();}
}
