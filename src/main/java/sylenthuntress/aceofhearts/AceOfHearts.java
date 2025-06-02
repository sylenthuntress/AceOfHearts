package sylenthuntress.aceofhearts;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sylenthuntress.aceofhearts.event.LifestealEvent;

public class AceOfHearts implements DedicatedServerModInitializer {
    public static final String MOD_ID = "aceofhearts";
    public static final String MOD_NAME = "AceOfHearts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier modIdentifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public void onInitializeServer() {
        AceOfHearts.LOGGER.info(MOD_NAME + " by SylentHuntress successfully loaded!");

        ServerLivingEntityEvents.AFTER_DEATH.register(new LifestealEvent());
    }
}