package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;

@SuppressWarnings("UnstableApiUsage")
public class GracePeriod implements ServerPlayerEvents.Join, ServerPlayerEvents.AfterRespawn {
    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        newPlayer.modifyAttached(ModAttachmentTypes.GRACE_PERIOD, period -> period + 6000);
    }

    @Override
    public void onJoin(ServerPlayerEntity player) {
        if (player.getAttachedOrCreate(ModAttachmentTypes.GRACE_PERIOD) == -1) {
            return;
        }

        player.modifyAttached(ModAttachmentTypes.GRACE_PERIOD, period -> period + 36000);
    }
}
