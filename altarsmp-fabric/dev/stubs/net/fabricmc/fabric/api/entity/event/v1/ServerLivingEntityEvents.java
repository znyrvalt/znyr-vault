package net.fabricmc.fabric.api.entity.event.v1;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
public final class ServerLivingEntityEvents {
    private ServerLivingEntityEvents() {}
    @FunctionalInterface public interface AfterDeath { void onDeath(LivingEntity entity, DamageSource source); }
    @FunctionalInterface public interface AfterDamage { void onDamage(LivingEntity entity, DamageSource source, float baseDamage, float damageTaken, boolean blocked); }
    public static final Event<AfterDeath> AFTER_DEATH = new Event<>();
    public static final Event<AfterDamage> AFTER_DAMAGE = new Event<>();
}
