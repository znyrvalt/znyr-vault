package net.minecraft.util;
public enum ActionResult { SUCCESS, PASS, FAIL;
    public boolean isAccepted() { return this == SUCCESS; } }
