package net.minecraft.registry;
import net.minecraft.util.Identifier;
import java.util.Map;
public class Registry<T> {
    private final Map<T, Identifier> byValue = new java.util.LinkedHashMap<>();
    private final Map<Identifier, T> map = new java.util.LinkedHashMap<>();
    public T get(Identifier id){ return map.get(id); }
    public boolean containsId(Identifier id){ return map.containsKey(id); }
    public void register(Identifier id, T v){ map.put(id, v); byValue.put(v, id); }
    public Identifier getId(T v){ return byValue.get(v); }
}
