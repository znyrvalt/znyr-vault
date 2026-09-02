package net.minecraft.entity;
public class EntityType<T extends Entity> {
    public static final EntityType<net.minecraft.entity.decoration.ArmorStandEntity> ARMOR_STAND = new EntityType<>();
    public static final EntityType<net.minecraft.entity.decoration.TextDisplayEntity> TEXT_DISPLAY = new EntityType<>();
}
