package sylenthuntress.aceofhearts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sylenthuntress.aceofhearts.util.LifestealHelper;

import java.util.Optional;

public class HeartCommand implements CommandRegistrationCallback {
    private static final DynamicCommandExceptionType NOT_ENOUGH_EXCEPTION = new DynamicCommandExceptionType(
            amount -> Text.stringifiedTranslatable("commands.heart.withdrawal.not_enough", amount)
    );

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        var cmdNode = CommandManager.literal("heart")
                .build();

        cmdNode.addChild(WithdrawNode.get());
        cmdNode.addChild(AddNode.get());

        dispatcher.getRoot().addChild(cmdNode);
    }

    public static class WithdrawNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("withdraw")
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                            .executes(context -> executeWithdraw(context.getSource(), IntegerArgumentType.getInteger(context, "amount"))))
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                            .requires(source -> source.hasPermissionLevel(2))
                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                    .executes(context -> executeWithdraw(EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))).build();
        }

        private static int executeWithdraw(ServerPlayerEntity player, int amount) throws CommandSyntaxException {
            if (LifestealHelper.getHearts(player) < amount + 1) {
                throw NOT_ENOUGH_EXCEPTION.create(LifestealHelper.getHearts(player));
            }

            for (int i = 0; i < amount; i++) {
                LifestealHelper.removeHeart(player, Optional.empty());
            }

            return amount;
        }

        private static int executeWithdraw(ServerCommandSource source, int amount) throws CommandSyntaxException {
            return executeWithdraw(source.getPlayerOrThrow(), amount);
        }
    }

    public static class AddNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("add")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                            .executes(context -> executeAdd(context.getSource(), IntegerArgumentType.getInteger(context, "amount"))))
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                            .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                    .executes(context -> executeAdd(EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))).build();
        }

        private static int executeAdd(ServerPlayerEntity player, int amount) throws CommandSyntaxException {
            LifestealHelper.addHeart(player, amount);
            return amount;
        }

        private static int executeAdd(ServerCommandSource source, int amount) throws CommandSyntaxException {
            return executeAdd(source.getPlayerOrThrow(), amount);
        }
    }
}
