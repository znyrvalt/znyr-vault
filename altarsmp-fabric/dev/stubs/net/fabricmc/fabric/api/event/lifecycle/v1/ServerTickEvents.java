package net.fabricmc.fabric.api.event.lifecycle.v1;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
public final class ServerTickEvents {
    private ServerTickEvents() {}
    @FunctionalInterface public interface EndTick { void onEndTick(MinecraftServer server); }
    @FunctionalInterface public interface WorldTickCallback { void onWorldTick(ServerWorld world); }
    public static final Event<EndTick> END_SERVER_TICK = new Event<>();
    public static final Event<WorldTickCallback> END_WORLD_TICK = new Event<>();
}
