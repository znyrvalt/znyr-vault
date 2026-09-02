package net.minecraft.server.network;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
public class ServerPlayerEntity extends PlayerEntity {
    public ServerPlayerEntity(EntityType<?> type, World w){super(type,w);}
}
