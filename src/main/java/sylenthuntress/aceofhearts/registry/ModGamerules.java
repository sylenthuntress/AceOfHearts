package sylenthuntress.aceofhearts.registry;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class ModGamerules {
    public static final GameRules.Key<GameRules.IntRule> MAX_HEARTS =
            GameRuleRegistry.register("maxStolenHearts", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(20));

    public static void registerAll() {

    }
}
