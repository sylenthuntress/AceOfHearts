package sylenthuntress.aceofhearts;


import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sylenthuntress.aceofhearts.command.HeartCommand;
import sylenthuntress.aceofhearts.event.LifestealEvent;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;
import sylenthuntress.aceofhearts.registry.ModGamerules;

public class AceOfHearts implements DedicatedServerModInitializer {
    public static final String MOD_ID = "aceofhearts";
    public static final String MOD_NAME = "AceOfHearts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier modIdentifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public void onInitializeServer() {
        AceOfHearts.LOGGER.info(MOD_NAME + " by SylentHuntress successfully loaded!");

        ModGamerules.registerAll();
        ModAttachmentTypes.registerAll();

        ServerLivingEntityEvents.AFTER_DEATH.register(new LifestealEvent());
        ServerPlayerEvents.AFTER_RESPAWN.register(new LifestealEvent());
        CommandRegistrationCallback.EVENT.register(new HeartCommand());
    }
}