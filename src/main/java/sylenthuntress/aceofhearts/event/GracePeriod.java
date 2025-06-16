package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;

@SuppressWarnings("UnstableApiUsage")
public class GracePeriod implements ServerLivingEntityEvents.AllowDamage, ServerPlayerEvents.Join, ServerPlayerEvents.AfterRespawn {
    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        newPlayer.setAttached(ModAttachmentTypes.GRACE_PERIOD, 6000);
    }

    @Override
    public void onJoin(ServerPlayerEntity player) {
        if (player.getAttachedOrCreate(ModAttachmentTypes.GRACE_PERIOD) == -1) {
            return;
        }

        player.modifyAttached(ModAttachmentTypes.GRACE_PERIOD, period -> period + 36000);
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
}
