package com.altarsmp.fabric.config;

import com.altarsmp.fabric.util.AltarSMPLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Flat-key configuration, mirroring the original Bukkit config sections
 * (e.g. "abilities.paladinbattleaxe.shatter_cooldown"). Defaults are shipped in
 * data/altarsmp/config_defaults.json (extracted verbatim from the authoritative config.yml).
 * Runtime edits live in config/altarsmp/config.json; keys not present there use defaults.
 */
public final class AltarSMPConfig {
    private final Map<String, String> defaults = new LinkedHashMap<>();
    private final Map<String, String> overrides = new LinkedHashMap<>();
    private Path effectivePath;
    private boolean debug;

    public void load(ClassLoader cl, Path gameConfigDir) {
        loadDefaults(cl);
        try {
            effectivePath = gameConfigDir.resolve("altarsmp").resolve("config.json");
            if (Files.exists(effectivePath)) {
                JsonObject obj = JsonParser.parseString(Files.readString(effectivePath)).getAsJsonObject();
                for (String k : obj.keySet()) overrides.put(k, obj.get(k).getAsString());
            }
        } catch (Exception e) {
            AltarSMPLog.error("Failed to read config overrides at " + effectivePath, e);
        }
        debug = getBoolean("debug", true);
        com.altarsmp.fabric.util.AltarSMPLog.AltarSMPConfigHolder.DEBUG = debug;
        AltarSMPLog.info("Config loaded: " + defaults.size() + " defaults, " + overrides.size() + " overrides, debug=" + debug);
    }

    private void loadDefaults(ClassLoader cl) {
        try (InputStream in = cl.getResourceAsStream("data/altarsmp/config_defaults.json")) {
            if (in == null) { AltarSMPLog.error("config_defaults.json missing", new IllegalStateException("missing resource")); return; }
            JsonObject obj = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            for (String k : obj.keySet()) defaults.put(k, obj.get(k).getAsString());
        } catch (Exception e) {
            AltarSMPLog.error("Failed to load config_defaults.json", e);
        }
    }

    public void saveDefaults(Path gameConfigDir) {
        try {
            Path dir = gameConfigDir.resolve("altarsmp");
            Files.createDirectories(dir);
            Path defaultsPath = dir.resolve("config.defaults.json");
            if (Files.notExists(defaultsPath)) {
                JsonObject obj = new JsonObject();
                for (Map.Entry<String, String> e : defaults.entrySet()) obj.addProperty(e.getKey(), e.getValue());
                Files.writeString(defaultsPath, new GsonBuilder().setPrettyPrinting().create().toJson(obj));
            }
        } catch (IOException e) {
            AltarSMPLog.error("Failed to write config.defaults.json", e);
        }
    }

    public String getString(String key, String def) {
        String v = overrides.get(key);
        if (v != null) return v;
        v = defaults.get(key);
        return v != null ? v : def;
    }

    public int getInt(String key, int def) { return (int) parseLong(getString(key, null), def); }
    public long getLong(String key, long def) { return parseLong(getString(key, null), def); }
    public double getDouble(String key, double def) {
        String v = getString(key, null);
        if (v == null) return def;
        try { return Double.parseDouble(v); } catch (NumberFormatException e) { return def; }
    }
    public boolean getBoolean(String key, boolean def) {
        String v = getString(key, null);
        if (v == null) return def;
        return Boolean.parseBoolean(v);
    }

    private static long parseLong(String v, long def) {
        if (v == null) return def;
        try { return Long.parseLong(v.trim()); } catch (NumberFormatException e) { return def; }
    }

    public boolean isDebug() { return debug; }
    public Map<String, String> defaults() { return defaults; }
}
