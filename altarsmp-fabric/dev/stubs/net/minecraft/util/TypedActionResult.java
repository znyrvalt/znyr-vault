package net.minecraft.util;
public record TypedActionResult<T>(ActionResult result, T value) {
    public static <T> TypedActionResult<T> pass(T v){ return new TypedActionResult<>(ActionResult.PASS, v); }
    public static <T> TypedActionResult<T> success(T v){ return new TypedActionResult<>(ActionResult.SUCCESS, v); }
}
