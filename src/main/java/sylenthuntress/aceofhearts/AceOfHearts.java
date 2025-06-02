package sylenthuntress.aceofhearts;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceOfHearts implements DedicatedServerModInitializer {
    public static final String MOD_ID = "aceofhearts";
    public static final String MOD_NAME = "AceOfHearts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier modIdentifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public void onInitializeServer() {
        AceOfHearts.LOGGER.info(MOD_NAME + " by SylentHuntress successfully loaded!");
    }
}