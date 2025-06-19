package sylenthuntress.aceofhearts;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sylenthuntress.aceofhearts.command.HeartCommand;
import sylenthuntress.aceofhearts.event.CustomLootTables;
import sylenthuntress.aceofhearts.event.DisallowMaceEnchantments;
import sylenthuntress.aceofhearts.event.GracePeriod;
import sylenthuntress.aceofhearts.event.LifestealEvent;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;
import sylenthuntress.aceofhearts.registry.ModGamerules;

public class AceOfHearts implements ModInitializer {
    public static final String MOD_ID = "aceofhearts";
    public static final String MOD_NAME = "AceOfHearts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier modIdentifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public void onInitialize() {
        AceOfHearts.LOGGER.info(MOD_NAME + " by SylentHuntress successfully loaded!");

        ModGamerules.registerAll();
        ModAttachmentTypes.registerAll();

        ServerLivingEntityEvents.AFTER_DEATH.register(new LifestealEvent());
        ServerPlayerEvents.AFTER_RESPAWN.register(new LifestealEvent());
        ServerPlayerEvents.JOIN.register(new LifestealEvent());
        LootTableEvents.MODIFY.register(new CustomLootTables());
        LootTableEvents.REPLACE.register(new CustomLootTables());
        EnchantmentEvents.ALLOW_ENCHANTING.register(new DisallowMaceEnchantments());
        CommandRegistrationCallback.EVENT.register(new HeartCommand());
        ServerPlayerEvents.AFTER_RESPAWN.register(new GracePeriod());
        ServerPlayerEvents.AFTER_RESPAWN.register(new GracePeriod());
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(new GracePeriod());
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(new GracePeriod());

        //noinspection CodeBlock2Expr
        DefaultItemComponentEvents.MODIFY.register(modifyContext -> {
            modifyContext.modify(Items.TOTEM_OF_UNDYING, builder -> builder.add(DataComponentTypes.DEATH_PROTECTION, null));
        });
    }
}