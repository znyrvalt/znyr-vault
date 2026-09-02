package net.minecraft.entity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
public class Entity {
    private final UUID uuid = UUID.randomUUID();
    private final Set<String> tags = new LinkedHashSet<>();
    public UUID getUuid(){return uuid;}
    public Set<String> getCommandTags(){return tags;}
    public void addCommandTag(String t){tags.add(t);}
    public void setCustomName(Text t){}
    public void setCustomNameVisible(boolean v){}
    public void setInvisible(boolean v){}
    public void setInvulnerable(boolean v){}
    public void setNoGravity(boolean v){}
    public void setGlowing(boolean v){}
    public void discard(){}
    public BlockPos getBlockPos(){return new BlockPos(0,0,0);}
    public void refreshPositionAndAngles(double x,double y,double z,float yaw,float pitch){}
    public Vec3d getPos(){return new Vec3d(0,0,0);}
    public float getYaw(){return 0;}
    public EntityType<?> getType(){return new EntityType<>();}
    public void setFireTicks(int ticks){}
    public boolean isOnFire(){return false;}
    public void extinguish(){}
    public boolean isInLava(){return false;}
    public void setVelocity(double x,double y,double z){}
    public Vec3d getVelocity(){return new Vec3d(0,0,0);}
    public double getX(){return 0;} public double getY(){return 0;} public double getZ(){return 0;}
    public Vec3d getRotationVector(){return new Vec3d(0,0,1);}
    public float distanceTo(Entity other){return 0;}
    public net.minecraft.entity.damage.DamageSources getDamageSources(){return new net.minecraft.entity.damage.DamageSources();}
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount){return false;}
    public boolean isSneaking(){return false;}
    public World getWorld(){return new World(false);}
}
