package net.minecraft.registry;
public class RegistryEntry<T> {
    private final T value;
    public RegistryEntry(T value){ this.value=value; }
    public static <T> RegistryEntry<T> of(T value){ return new RegistryEntry<>(value); }
    public T getValue(){ return value; }
}
