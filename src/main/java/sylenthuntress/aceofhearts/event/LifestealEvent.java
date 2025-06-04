package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;
import sylenthuntress.aceofhearts.util.LifestealHelper;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class LifestealEvent implements ServerLivingEntityEvents.AfterDeath, ServerPlayerEvents.AfterRespawn {
    @Override
    public void afterDeath(LivingEntity entity, DamageSource damageSource) {
        entity.setAttached(ModAttachmentTypes.DEATH_COORDS, entity.getBlockPos());
        if (!(entity instanceof ServerPlayerEntity player) || player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        Optional<ServerPlayerEntity> source = Optional.empty();
        if (damageSource.getSource() instanceof ServerPlayerEntity sourcePlayer) {
            source = Optional.of(sourcePlayer);
        }

        LifestealHelper.removeHeart(player, source, true);
    }

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        LifestealHelper.recalculateHealth(newPlayer);

        if (oldPlayer.getAttachedOrElse(ModAttachmentTypes.DEAD, false)) {
            newPlayer.changeGameMode(GameMode.SURVIVAL);
            LifestealHelper.setHearts(newPlayer, 1);
            return;
        }

        if (LifestealHelper.getHearts(newPlayer) == 0) {
            newPlayer.setAttached(ModAttachmentTypes.DEAD, true);
            newPlayer.changeGameMode(GameMode.SPECTATOR);
            newPlayer.sendMessage(Text.translatable("aceofhearts.player.death_message"), false);
        }
    }
}
