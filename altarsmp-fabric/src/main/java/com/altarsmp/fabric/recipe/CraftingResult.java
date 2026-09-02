package com.altarsmp.fabric.recipe;

import java.util.Collections;
import java.util.List;

/** Outcome of a transactional altar craft. */
public record CraftingResult(Status status, List<String> missing, String weaponId) {
    public enum Status { OK, UNKNOWN_RECIPE, NO_FACTION, NO_ALTAR_NEARBY, MISSING_ITEMS, INVENTORY_FULL, ERROR }

    public static CraftingResult ok(String weaponId) { return new CraftingResult(Status.OK, Collections.emptyList(), weaponId); }
    public static CraftingResult missing(List<String> missing) { return new CraftingResult(Status.MISSING_ITEMS, missing, null); }
}
