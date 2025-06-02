package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import sylenthuntress.aceofhearts.util.LifestealHelper;

import java.util.Optional;

public class LifestealEvent implements ServerLivingEntityEvents.AfterDeath {
    @Override
    public void afterDeath(LivingEntity entity, DamageSource damageSource) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        Optional<ServerPlayerEntity> source = Optional.empty();
        if (damageSource.getSource() instanceof ServerPlayerEntity sourcePlayer) {
            source = Optional.of(sourcePlayer);
        }

        LifestealHelper.removeHeart(player, source);
    }
}
