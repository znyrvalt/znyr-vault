package net.fabricmc.fabric.api.command.v2;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.command.CommandManager;
@FunctionalInterface
public interface CommandRegistrationCallback {
    void register(CommandManager dispatcher, net.minecraft.command.CommandRegistryAccess registryAccess, net.fabricmc.api.Environment environment);
    Event<CommandRegistrationCallback> EVENT = new Event<>();
}
