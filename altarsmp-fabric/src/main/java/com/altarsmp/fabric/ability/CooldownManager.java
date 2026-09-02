package com.altarsmp.fabric.ability;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory per-player per-ability cooldown registry (seconds resolution, original ms precision). */
public final class CooldownManager {
    private final Map<String, Long> used = new ConcurrentHashMap<>(); // player|ability -> epoch ms

    public long remainingMs(UUID player, String abilityId, int cooldownSeconds) {
        Long t = used.get(key(player, abilityId));
        if (t == null) return 0;
        long elapsed = System.currentTimeMillis() - t;
        long total = cooldownSeconds * 1000L;
        return elapsed >= total ? 0 : total - elapsed;
    }

    public boolean tryUse(UUID player, String abilityId, int cooldownSeconds) {
        if (remainingMs(player, abilityId, cooldownSeconds) > 0) return false;
        used.put(key(player, abilityId), System.currentTimeMillis());
        return true;
    }

    public void reset(UUID player, String abilityId) { used.remove(key(player, abilityId)); }

    private static String key(UUID player, String abilityId) { return player + "|" + abilityId; }
}
