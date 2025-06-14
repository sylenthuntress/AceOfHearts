package sylenthuntress.aceofhearts.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;
import sylenthuntress.aceofhearts.util.LifestealHelper;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class Mixin_ServerPlayerEntity extends PlayerEntity {
    public Mixin_ServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow public abstract boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, boolean resetCamera);

    @Shadow public abstract void sendMessageToClient(Text message, boolean overlay);

    @Shadow public abstract void sendMessage(Text message, boolean overlay);

    @Shadow public abstract ServerWorld getServerWorld();

    @Shadow public abstract void playSoundToPlayer(SoundEvent sound, SoundCategory category, float volume, float pitch);

    @Shadow public abstract void addEnchantedHitParticles(Entity target);

    @SuppressWarnings("UnstableApiUsage")
    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    public void teleportToDeathpoint(CallbackInfo ci) {
        if (this.getLastDeathPos().isEmpty() || !this.getAttachedOrElse(ModAttachmentTypes.DEAD, false)) {
             return;
        }

        BlockPos lastDeathPos = this.getLastDeathPos().get().pos();

        for (Entity entity : this.getWorld().getOtherEntities(this, Box.from(lastDeathPos.toCenterPos()).expand(6))) {
            if (!entity.isOnGround() || !(entity instanceof ItemEntity itemEntity) || itemEntity.getOwner() == this) {
                continue;
            }

            if (LifestealHelper.isRevivalTotem(itemEntity.getStack())) {
                MinecraftServer server = getServer();
                if (server != null) {
                    server.getPlayerManager().broadcast(
                            Text.translatable("aceofhearts.player.revive_message",
                                    MutableText.of(this.getName().getContent()).formatted(Formatting.GRAY),
                                    (itemEntity.getOwner() == null ? Text.literal("Unknown") : MutableText.of(itemEntity.getOwner().getName().getContent()))
                                            .formatted(Formatting.DARK_GRAY),
                                    LifestealHelper.getRevivalTotem().getFormattedName()
                            ).formatted(Formatting.GOLD), false
                    );
                }

                LightningEntity lightning = EntityType.LIGHTNING_BOLT.spawn(this.getServerWorld(), lastDeathPos, SpawnReason.EVENT);
                if (lightning != null) {
                    lightning.setCosmetic(true);
                    lightning.setChanneler((ServerPlayerEntity) (Object) this);
                }

                this.addDeathParticles();
                this.playSoundToPlayer(
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,
                        SoundCategory.PLAYERS,
                        1, 1
                );

                this.setHealth(0.0F);
                entity.discard();
                return;
            }
        }

        this.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, -1, 0, true, false, true));
        this.sendMessage(Text.literal("You are dead!").formatted(Formatting.RED), true);
        this.teleport(lastDeathPos.getX(),lastDeathPos.getY(),lastDeathPos.getZ(),false);
    }
}
