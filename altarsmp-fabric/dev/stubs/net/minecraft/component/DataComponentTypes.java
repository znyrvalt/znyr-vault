package net.minecraft.component;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
public final class DataComponentTypes {
    private DataComponentTypes() {}
    public static final ComponentType<NbtCompound> CUSTOM_DATA = new ComponentType<>(NbtCompound.class);
    public static final ComponentType<CustomModelDataComponent> CUSTOM_MODEL_DATA = new ComponentType<>(CustomModelDataComponent.class);
    public static final ComponentType<Text> CUSTOM_NAME = new ComponentType<>(Text.class);
    public static final ComponentType<Text> ITEM_NAME = new ComponentType<>(Text.class);
    public static final ComponentType<LoreComponent> LORE = new ComponentType<>(LoreComponent.class);
    public static final ComponentType<Rarity> RARITY = new ComponentType<>(Rarity.class);
}
