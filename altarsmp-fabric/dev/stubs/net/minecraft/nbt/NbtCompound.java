package net.minecraft.nbt;
import java.util.LinkedHashMap;
import java.util.Map;
public class NbtCompound implements NbtElement {
    private final Map<String, NbtElement> map = new LinkedHashMap<>();
    public NbtCompound copy() { NbtCompound c = new NbtCompound(); c.map.putAll(map); return c; }
    public boolean contains(String k){return map.containsKey(k);}
    public NbtCompound getCompound(String k){ NbtElement e=map.get(k); return e instanceof NbtCompound c?c:new NbtCompound(); }
    public String getString(String k){ NbtElement e=map.get(k); return e instanceof NbtString s?s.value:""; }
    public int getInt(String k){ NbtElement e=map.get(k); return e instanceof NbtInt i?i.value:0; }
    public void put(String k, NbtElement v){ map.put(k,v); }
    public void putString(String k,String v){ map.put(k,new NbtString(v)); }
    public void putInt(String k,int v){ map.put(k,new NbtInt(v)); }
    public String toString(){ return String.valueOf(map); }
}
