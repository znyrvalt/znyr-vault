package com.altarsmp.fabric.ability;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * One weapon ability. Triggers map to the original event surface:
 * SWAP = original PlayerSwapHandItemsEvent (trigger 1), SNEAK state = trigger 2,
 * LEFT_CLICK / RIGHT_CLICK / KILL / TICK are the native event entry points.
 */
public interface Ability {
    String weaponId();
    String id();
    List<Trigger> triggers();

    default void onSwap(ServerPlayerEntity player, ItemStack stack) {}
    default void onSneak(ServerPlayerEntity player, ItemStack stack, boolean sneaking) {}
    default void onLeftClick(ServerPlayerEntity player, ItemStack stack, net.minecraft.entity.Entity target) {}
    default void onRightClick(ServerPlayerEntity player, ItemStack stack) {}
    default void onKill(ServerPlayerEntity player, ItemStack stack, net.minecraft.entity.LivingEntity victim) {}
    default void onTick(ServerPlayerEntity player, ItemStack stack) {}

    enum Trigger { SWAP, SNEAK, LEFT_CLICK, RIGHT_CLICK, KILL, TICK }
}
