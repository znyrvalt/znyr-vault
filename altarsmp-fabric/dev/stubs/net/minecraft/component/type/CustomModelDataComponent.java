package net.minecraft.component.type;
import java.util.List;
import java.util.UUID;
public record CustomModelDataComponent(List<Float> floats, byte flags, List<String> strings, List<UUID> colors) {
    public static final CustomModelDataComponent DEFAULT = new CustomModelDataComponent(List.of(), (byte)0, List.of(), List.of());
}
