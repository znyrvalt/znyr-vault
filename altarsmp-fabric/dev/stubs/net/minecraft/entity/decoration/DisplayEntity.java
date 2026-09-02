package net.minecraft.entity.decoration;
import net.minecraft.entity.Entity;
public class DisplayEntity extends Entity {
    public enum BillboardMode { FIXED, VERTICAL, HORIZONTAL, CENTER }
    public void setBillboardMode(BillboardMode mode){}
}
