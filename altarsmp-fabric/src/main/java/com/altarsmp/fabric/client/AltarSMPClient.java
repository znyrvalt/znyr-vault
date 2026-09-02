package com.altarsmp.fabric.client;

import com.altarsmp.fabric.util.AltarSMPLog;
import net.fabricmc.api.ClientModInitializer;

/**
 * Client entrypoint. The resource pack ships inside the mod jar (assets are authoritative);
 * custom tooltip sprites and skybox rendering will be wired via client events in later
 * milestones — nothing is registered as a placeholder here.
 */
public final class AltarSMPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AltarSMPLog.info("AltarSMP client initialized (assets bundled in mod jar)");
    }
}
