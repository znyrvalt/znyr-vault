package net.minecraft.component;
public final class ComponentType<T> {
    private final Class<T> cls;
    public ComponentType(Class<T> cls){this.cls=cls;}
    public Class<T> getType(){return cls;}
}
