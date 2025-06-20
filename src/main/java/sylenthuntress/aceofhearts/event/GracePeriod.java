package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.dimension.DimensionTypes;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;

@SuppressWarnings("UnstableApiUsage")
public class GracePeriod implements ServerEntityWorldChangeEvents.AfterPlayerChange, ServerLivingEntityEvents.AllowDamage, ServerPlayerEvents.Join, ServerPlayerEvents.AfterRespawn {
    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        newPlayer.setAttached(ModAttachmentTypes.GRACE_PERIOD, 6000);
    }

    @Override
    public void onJoin(ServerPlayerEntity player) {
        if (player.getAttachedOrElse(ModAttachmentTypes.GRACE_PERIOD, -1) == -1) {
            player.setAttached(ModAttachmentTypes.GRACE_PERIOD, 36000);
            return;
        }

        player.modifyAttached(ModAttachmentTypes.GRACE_PERIOD, duration -> duration + 300);
    }

    private Entity prevAttacker;

    @Override
    public boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
        Entity attacker = source.getAttacker();
        int gracePeriod = entity.getAttachedOrCreate(ModAttachmentTypes.GRACE_PERIOD);

        if (attacker != null && attacker.getType() == EntityType.PLAYER) {
            if (gracePeriod > 0) {
                if (attacker instanceof PlayerEntity player) {
                    player.sendMessage(Text.translatable("aceofhearts.player.grace_period.attacker", gracePeriod / 20).formatted(Formatting.RED), false);
                }

                if (prevAttacker == attacker) {
                    attacker.damage((ServerWorld) attacker.getWorld(), source, amount);
                } else prevAttacker = attacker;

                return false;
            }
        }

        return true;
    }

    @Override
    public void afterChangeWorld(ServerPlayerEntity player, ServerWorld oldWorld, ServerWorld newWorld) {
        var registry = newWorld.getRegistryManager().getOrThrow(RegistryKeys.DIMENSION_TYPE);
        if (registry.get(DimensionTypes.THE_END) == newWorld.getDimension()) {
            player.modifyAttached(ModAttachmentTypes.GRACE_PERIOD, duration -> duration + 600);
        }
    }
}
