package net.minecraft.entity;
import net.minecraft.item.ItemStack;
public class ItemEntity extends Entity {
    public ItemStack getStack(){ return new ItemStack(new net.minecraft.item.Item("stub"),1); }
    public void setNeverDespawn(){}
    public void setPickupDelay(int ticks){}
}
