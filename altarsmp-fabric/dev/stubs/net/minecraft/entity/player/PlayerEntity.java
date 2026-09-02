package net.minecraft.entity.player;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import com.mojang.authlib.GameProfile;
public class PlayerEntity extends LivingEntity {
    private final PlayerInventory inv = new PlayerInventory();
    public PlayerEntity(EntityType<?> type, World world){super(type,world);}
    public PlayerInventory getInventory(){return inv;}
    public void sendMessage(Text t){}
    public void sendMessage(Text t, boolean overlay){}
    public ServerWorld getServerWorld(){return new ServerWorld();}
    public ItemEntity dropItem(ItemStack s, boolean throwRandomly){return new ItemEntity();}
    public GameProfile getGameProfile(){return new GameProfile("stub");}
    public ItemStack getMainHandStack(){return new ItemStack(new net.minecraft.item.Item("stub"));}
    public ItemStack getStackInHand(net.minecraft.util.Hand hand){return getMainHandStack();}
    public void removeCommandTag(String tag){}
    public boolean isTouchingWater(){return false;}
    public net.minecraft.entity.attribute.AttributeInstance getAttributeInstance(net.minecraft.registry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attr){return new net.minecraft.entity.attribute.AttributeInstance();}
}
