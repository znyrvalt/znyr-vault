package net.minecraft.entity.attribute;
import net.minecraft.registry.RegistryEntry;
public final class EntityAttributes {
    private EntityAttributes() {}
    public static final RegistryEntry<EntityAttribute> GENERIC_MAX_HEALTH = RegistryEntry.of(new EntityAttribute());
}
