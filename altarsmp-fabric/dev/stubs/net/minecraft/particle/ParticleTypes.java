package net.minecraft.particle;
public final class ParticleTypes {
    private ParticleTypes() {}
    public static final ParticleEffect EXPLOSION = new SimpleParticle();
    public static final ParticleEffect LARGE_SMOKE = new SimpleParticle();
    public static final ParticleEffect CRIT = new SimpleParticle();
    private static final class SimpleParticle implements ParticleEffect {}
}
