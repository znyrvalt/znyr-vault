package net.minecraft.server.world;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import java.util.UUID;
public class ServerWorld extends World {
    public ServerWorld(){super(false);}
    public <E extends Entity> E spawnEntity(E e){return e;}
    public Entity getEntity(UUID id){return null;}
    public Iterable<Entity> iterateEntities(){return java.util.Collections.emptyList();}
    public void spawnParticles(net.minecraft.particle.ParticleEffect effect, double x,double y,double z,int count,double dx,double dy,double dz,double speed){}
    public void playSound(net.minecraft.entity.player.PlayerEntity player, double x,double y,double z, net.minecraft.sound.SoundEvent sound, net.minecraft.sound.SoundCategory cat, float volume, float pitch){}
    public long getTimeOfDay(){return 0;}
    public int getSkyLight(net.minecraft.util.math.BlockPos pos){return 15;}
    public boolean isRaining(){return false;}
    public boolean isSkyVisible(net.minecraft.util.math.BlockPos pos){return true;}
    public net.minecraft.block.BlockState getBlockState(net.minecraft.util.math.BlockPos pos){return new net.minecraft.block.BlockState();}
    public java.util.List<net.minecraft.entity.LivingEntity> getEntitiesByType(net.minecraft.predicate.entity.TypeFilter<net.minecraft.entity.Entity, net.minecraft.entity.LivingEntity> filter, net.minecraft.util.math.Box box, java.util.function.Predicate<? super net.minecraft.entity.LivingEntity> predicate){return java.util.Collections.emptyList();}
}

