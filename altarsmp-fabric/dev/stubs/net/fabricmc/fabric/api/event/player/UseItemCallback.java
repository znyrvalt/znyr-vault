package net.fabricmc.fabric.api.event.player;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
@FunctionalInterface
public interface UseItemCallback {
    TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand);
    Event<UseItemCallback> EVENT = new Event<>();
}
