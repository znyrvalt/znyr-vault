package net.minecraft.predicate.entity;
public interface TypeFilter<T, S extends T> {
    static <T, S extends T> TypeFilter<T, S> instanceOf(Class<S> cls){ return new TypeFilter<>() {}; }
}
