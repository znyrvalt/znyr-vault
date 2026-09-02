package net.minecraft.entity.player;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
public class PlayerInventory {
    public final List<ItemStack> main = new ArrayList<>(36);
    public PlayerInventory(){ for(int i=0;i<36;i++) main.add(new ItemStack(null)); }
    public int addStack(ItemStack s){ return 0; }
    public int size(){ return main.size(); }
    public ItemStack getStack(int i){ return main.get(i); }
    public void removeStack(int i, int count){}
    public void offerOrDrop(ItemStack s){}
}
