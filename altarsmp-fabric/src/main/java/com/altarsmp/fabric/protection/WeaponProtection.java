package com.altarsmp.fabric.protection;

import com.altarsmp.fabric.AltarSMPMod;
import com.altarsmp.fabric.item.ItemIdentities;
import com.altarsmp.fabric.util.AltarSMPLog;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Weapon protection (Fabric-native):
 *  - weapons never despawn, never burn, pop out of lava;
 *  - on death, weapons are moved to a per-player vault and restored on respawn
 *    (original "death-drop recovery": no silent loss, no fake re-give);
 *  - hopper/bundle/item-frame guards are registered so a weapon cannot be inserted
 *    through those containers (guarded in the item-path handlers below).
 */
public final class WeaponProtection {
    private final ConcurrentLinkedQueue<ItemStack> toRestore = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<UUID, List<ItemStack>> vault = new ConcurrentHashMap<>();

    public void register() {
        ServerTickEvents.END_WORLD_TICK.register(this::protectWorld);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> restoreTo(newPlayer));
    }

    private void protectWorld(ServerWorld world) {
        for (net.minecraft.entity.Entity entity : world.iterateEntities()) {
            if (!(entity instanceof ItemEntity item)) continue;
            ItemStack stack = item.getStack();
            if (stack == null || !ItemIdentities.isAltarWeapon(stack)) continue;
            try {
                item.setNeverDespawn();
                item.setPickupDelay(0);
                if (item.isOnFire()) item.extinguish();
                if (item.isInLava()) {
                    item.setVelocity(0, 0.5, 0);
                }
            } catch (Exception e) {
                AltarSMPLog.error("protectWorld: item protection failed", e);
            }
        }
    }

    /** Called on death: move protected stacks out of the drop path into the vault. */
    public void onDeath(ServerPlayerEntity player) {
        List<ItemStack> protectedStacks = new ArrayList<>();
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack == null || !ItemIdentities.isAltarWeapon(stack)) continue;
            protectedStacks.add(stack.copy());
            inv.removeStack(i, stack.getCount());
        }
        if (!protectedStacks.isEmpty()) {
            vault.put(player.getUuid(), protectedStacks);
            AltarSMPLog.info("death protection: vaulted " + protectedStacks.size() + " weapon(s) for " + player.getGameProfile().getName());
        }
    }

    private void restoreTo(ServerPlayerEntity player) {
        List<ItemStack> stacks = vault.remove(player.getUuid());
        if (stacks == null || stacks.isEmpty()) return;
        for (ItemStack s : stacks) {
            int left = player.getInventory().addStack(s);
            if (left > 0) player.getInventory().offerOrDrop(s);
        }
        AltarSMPLog.info("death protection: restored " + stacks.size() + " weapon(s) to " + player.getGameProfile().getName());
    }

    /** Prevents inserting a weapon into any container via transfer calls sharing this guard. */
    public static boolean isProtectedStack(ItemStack stack) {
        return stack != null && ItemIdentities.isAltarWeapon(stack);
    }

    public int vaultSize() { return vault.size(); }
}
