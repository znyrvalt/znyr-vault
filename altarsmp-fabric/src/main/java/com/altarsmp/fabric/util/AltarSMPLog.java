package com.altarsmp.fabric.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Central logger. Every subsystem logs through here; nothing is silently swallowed. */
public final class AltarSMPLog {
    public static final Logger LOG = LoggerFactory.getLogger("AltarSMP");

    private AltarSMPLog() {}

    public static void info(String msg) { LOG.info("[AltarSMP] {}", msg); }
    public static void warn(String msg) { LOG.warn("[AltarSMP] {}", msg); }
    public static void error(String msg, Throwable t) { LOG.error("[AltarSMP] {}", msg, t); }
    public static void debug(String msg) {
        if (AltarSMPConfigHolder.DEBUG) LOG.info("[AltarSMP][debug] {}", msg);
    }
    /** Debug flag set from config after config load. */
    public static final class AltarSMPConfigHolder {
        private AltarSMPConfigHolder() {}
        public static volatile boolean DEBUG = false;
    }
}
