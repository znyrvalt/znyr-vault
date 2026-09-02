package com.altarsmp.fabric.altar;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/** Tracks one spawned altar (armor-stand marker + hologram). */
public record AltarInfo(UUID standUuid, UUID hologramUuid, String altarId, BlockPos pos) {}
