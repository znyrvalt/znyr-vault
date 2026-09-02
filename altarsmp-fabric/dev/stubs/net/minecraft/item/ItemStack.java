package net.minecraft.item;
import net.minecraft.component.ComponentType;
import java.util.LinkedHashMap;
import java.util.Map;
public class ItemStack {
    private final Item item;
    private int count;
    private final Map<ComponentType<?>, Object> components = new LinkedHashMap<>();
    public ItemStack(Item item){ this(item,1); }
    public ItemStack(Item item, int count){ this.item=item; this.count=count; }
    public Item getItem(){ return item; }
    public int getCount(){ return count; }
    public void setCount(int c){ count=c; }
    public void decrement(int n){ count=Math.max(0,count-n); }
    public boolean isEmpty(){ return count<=0; }
    public <T> T get(ComponentType<T> t){ return (T) components.get(t); }
    public <T> T getOrDefault(ComponentType<T> t, T def){ return components.containsKey(t) ? (T) components.get(t) : def; }
    public <T> void set(ComponentType<T> t, T v){ components.put(t,v); }
    public boolean contains(ComponentType<?> t){ return components.containsKey(t); }
    public void addEnchantment(net.minecraft.registry.RegistryEntry<net.minecraft.enchantment.Enchantment> enchantment, int level){}
    public ItemStack copy(){ ItemStack c=new ItemStack(item,count); c.components.putAll(components); return c; }
}
