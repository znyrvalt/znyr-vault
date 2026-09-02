package com.altarsmp.fabric.altar;

import com.altarsmp.fabric.AltarSMPMod;
import com.altarsmp.fabric.util.AltarIds;
import com.altarsmp.fabric.util.AltarSMPLog;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.TextDisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Altar spawning/cleanup. Altars are the same construct as the original: a (now invisible,
 * invulnerable, no-gravity) armor-stand marker with an item/text display hologram above it.
 * The armor stand carries command tags {@code asmp_altar} + {@code asmp_altar_<id>} so server
 * ops can identify/clean them, and its custom name holds the altar display id.
 */
public final class AltarManager {
    private final Map<UUID, AltarInfo> altars = new ConcurrentHashMap<>();

    public AltarInfo spawn(ServerWorld world, double x, double y, double z, float yaw, String altarId, String displayName) {
        double holoHeight = AltarSMPMod.config().getDouble("altar.hologram_height", 2.5);
        double rotationSpeed = AltarSMPMod.config().getDouble("altar.rotation_speed", 2.0);

        ArmorStandEntity stand = new ArmorStandEntity(EntityType.ARMOR_STAND, world);
        stand.setCustomName(Text.literal(displayName));
        stand.setCustomNameVisible(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setNoGravity(true);
        stand.addCommandTag(AltarIds.ALTAR_TAG);
        stand.addCommandTag(AltarIds.altarTag(altarId));
        stand.refreshPositionAndAngles(x, y, z, yaw, 0.0F);
        world.spawnEntity(stand);

        TextDisplayEntity holo = new TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
        holo.setText(Text.literal(displayName));
        holo.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
        holo.setInvisible(false);
        holo.setNoGravity(true);
        holo.setInvulnerable(true);
        holo.addCommandTag(AltarIds.ALTAR_TAG);
        holo.addCommandTag(AltarIds.altarTag(altarId));
        holo.refreshPositionAndAngles(x, y + holoHeight, z, yaw, 0.0F);
        world.spawnEntity(holo);

        AltarInfo info = new AltarInfo(stand.getUuid(), holo.getUuid(), altarId, stand.getBlockPos());
        altars.put(stand.getUuid(), info);
        altars.put(holo.getUuid(), info);
        AltarSMPLog.info("Altar spawned: " + altarId + " at " + x + "," + y + "," + z);
        return info;
    }

    public String altarIdOfEntity(net.minecraft.entity.Entity entity) {
        if (!entity.getCommandTags().contains(AltarIds.ALTAR_TAG)) return null;
        for (String tag : entity.getCommandTags()) {
            if (tag.startsWith(AltarIds.ALTAR_TAG_PREFIX)) return tag.substring(AltarIds.ALTAR_TAG_PREFIX.length());
        }
        return null;
    }

    public void removeNear(ServerWorld world, double x, double y, double z, String altarId, double radiusSq) {
        Iterator<AltarInfo> it = altars.values().iterator();
        boolean removed = false;
        while (it.hasNext()) {
            AltarInfo info = it.next();
            if (!info.altarId().equals(altarId)) continue;
            double dx = info.pos().getX() - x, dy = info.pos().getY() - y, dz = info.pos().getZ() - z;
            if (dx * dx + dy * dy + dz * dz > radiusSq) continue;
            discard(world, info.standUuid());
            discard(world, info.hologramUuid());
            it.remove();
            removed = true;
        }
        if (removed) AltarSMPLog.info("Removed altar(s): " + altarId);
    }

    public void cleanupAll(ServerWorld world) {
        for (AltarInfo info : altars.values()) {
            discard(world, info.standUuid());
            discard(world, info.hologramUuid());
        }
        altars.clear();
        AltarSMPLog.info("All altars cleaned up.");
    }

    private void discard(ServerWorld world, UUID uuid) {
        var entity = world.getEntity(uuid);
        if (entity != null) entity.discard();
    }

    public int count() { return altars.size() / 2; }
    public void onUnload(ServerWorld world) { cleanupAll(world); }
}
