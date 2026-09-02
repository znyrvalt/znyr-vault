package net.minecraft.entity.attribute;
import java.util.UUID;
public class EntityAttributeModifier {
    public enum Operation { ADD_VALUE, ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL }
    public EntityAttributeModifier(UUID id, String name, double value, Operation op){}
}
