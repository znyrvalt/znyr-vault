package net.minecraft.component.type;
import net.minecraft.text.Text;
import java.util.List;
public record LoreComponent(List<Text> lines) { public static final LoreComponent DEFAULT = new LoreComponent(List.of()); }
