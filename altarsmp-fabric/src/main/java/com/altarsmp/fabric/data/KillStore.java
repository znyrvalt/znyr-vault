package com.altarsmp.fabric.data;

import com.altarsmp.fabric.util.AltarSMPLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Persistent per-player kill totals per weapon (config/altarsmp/state/kills.json). */
public final class KillStore {
    private final Map<String, Map<String, Integer>> kills = new LinkedHashMap<>(); // uuid -> weaponId -> kills
    private Path path;

    public void setStorage(Path gameConfigDir) {
        path = gameConfigDir.resolve("altarsmp").resolve("state").resolve("kills.json");
    }

    public void load(Path gameConfigDir) {
        setStorage(gameConfigDir);
        if (path == null || !Files.exists(path)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            for (String uid : root.keySet()) {
                JsonObject w = root.getAsJsonObject(uid);
                Map<String, Integer> m = new LinkedHashMap<>();
                for (String weapon : w.keySet()) m.put(weapon, w.get(weapon).getAsInt());
                kills.put(uid, m);
            }
            AltarSMPLog.info("Kill store loaded: " + kills.size() + " players");
        } catch (Exception e) {
            AltarSMPLog.error("Failed to load kills store", e);
        }
    }

    public void record(UUID player, String weaponId, int total) {
        kills.computeIfAbsent(player.toString(), k -> new LinkedHashMap<>()).put(weaponId, total);
        save();
    }

    public int total(UUID player, String weaponId) {
        var m = kills.get(player.toString());
        return m == null ? 0 : m.getOrDefault(weaponId, 0);
    }

    public void save() {
        if (path == null) return;
        try {
            Files.createDirectories(path.getParent());
            JsonObject root = new JsonObject();
            for (Map.Entry<String, Map<String, Integer>> e : kills.entrySet()) {
                JsonObject w = new JsonObject();
                for (Map.Entry<String, Integer> k : e.getValue().entrySet()) w.addProperty(k.getKey(), k.getValue());
                root.add(e.getKey(), w);
            }
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(root), StandardCharsets.UTF_8);
        } catch (IOException e) {
            AltarSMPLog.error("Failed to save kills store", e);
        }
    }

    public int playerCount() { return kills.size(); }
}
