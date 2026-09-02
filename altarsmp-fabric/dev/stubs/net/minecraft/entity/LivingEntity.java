package net.minecraft.entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
public class LivingEntity extends Entity {
    public LivingEntity(EntityType<?> type, World world){}
    public void addStatusEffect(net.minecraft.entity.effect.StatusEffectInstance effect){}
    public boolean hasStatusEffect(net.minecraft.entity.effect.StatusEffect effect){return false;}
}
