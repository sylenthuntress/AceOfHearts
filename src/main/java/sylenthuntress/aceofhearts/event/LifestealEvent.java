package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;
import sylenthuntress.aceofhearts.util.LifestealHelper;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class LifestealEvent implements ServerLivingEntityEvents.AfterDeath, ServerPlayerEvents.AfterRespawn, ServerPlayerEvents.Join {
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
        if (LifestealHelper.getHearts(player) <= 0) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                player.playSound(
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(),
                        0.5F, 0.5F
                );

                server.getPlayerManager().broadcast(
                        Text.translatable("aceofhearts.player.perma_killed",
                                MutableText.of(player.getName().getContent()).formatted(Formatting.GRAY))
                                .formatted(Formatting.RED),
                        false
                );
            }
        }
    }

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        LifestealHelper.setHearts(newPlayer, LifestealHelper.getHearts(oldPlayer));
        LifestealHelper.recalculateHealth(newPlayer);

        if (LifestealHelper.isDead(oldPlayer)) {
            newPlayer.getWorld().sendEntityStatus(newPlayer, EntityStatuses.USE_TOTEM_OF_UNDYING);
            newPlayer.changeGameMode(GameMode.SURVIVAL);
            LifestealHelper.setHearts(newPlayer, 6);
            return;
        }

        if (LifestealHelper.getHearts(oldPlayer) == 0) {
            newPlayer.setAttached(ModAttachmentTypes.DEAD, true);
            newPlayer.changeGameMode(GameMode.SPECTATOR);

            newPlayer.sendMessage(
                    Text.translatable("aceofhearts.player.death_message.1", Text.translatable("aceofhearts.player.death_message.2")
                            .formatted(Formatting.DARK_RED, Formatting.BOLD), LifestealHelper.getRevivalTotem().getFormattedName()
                    ).formatted(Formatting.GRAY),
                    false
            );
        }
    }

    @Override
    public void onJoin(ServerPlayerEntity player) {
        LifestealHelper.recalculateHealth(player);
        if (LifestealHelper.isDead(player)) {
            player.sendMessage(
                    Text.translatable("aceofhearts.player.death_message.1",
                            Text.translatable("aceofhearts.player.death_message.2", LifestealHelper.getRevivalTotem().getFormattedName())
                                    .formatted(Formatting.GRAY, Formatting.ITALIC)
                    ).formatted(Formatting.DARK_RED),
                    false
            );
        }
    }
}
