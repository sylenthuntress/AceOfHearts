package sylenthuntress.aceofhearts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sylenthuntress.aceofhearts.util.LifestealHelper;

import java.util.Optional;

public class HeartCommand implements CommandRegistrationCallback {
    private static final DynamicCommandExceptionType NOT_ENOUGH_EXCEPTION = new DynamicCommandExceptionType(
            amount -> Text.stringifiedTranslatable("commands.hearts.withdrawal.not_enough", amount)
    );

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        var cmdNode = CommandManager.literal("hearts")
                .build();

        cmdNode.addChild(WithdrawNode.get());
        cmdNode.addChild(AddNode.get());
        cmdNode.addChild(SetNode.get());
        cmdNode.addChild(GetNode.get());
        cmdNode.addChild(GiveNode.get());

        dispatcher.getRoot().addChild(cmdNode);
    }

    public static class WithdrawNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("withdraw")
                    .executes(context -> executeWithdraw(context.getSource(), 1, false))
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                            .executes(context -> executeWithdraw(context.getSource(), IntegerArgumentType.getInteger(context, "amount"), false))
                            .then(CommandManager.literal("drop_item")
                                    .executes(context -> executeWithdraw(context.getSource(), IntegerArgumentType.getInteger(context, "amount"), true)))
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .executes(context -> executeWithdraw(context.getSource(), EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"), false))
                                    .then(CommandManager.literal("drop_item")
                                            .executes(context -> executeWithdraw(context.getSource(), IntegerArgumentType.getInteger(context, "amount"), true))))).build();
        }

        private static int executeWithdraw(ServerCommandSource source, ServerPlayerEntity player, int amount, boolean dropItem) throws CommandSyntaxException {
            if (LifestealHelper.getHearts(player) < amount + 1) {
                throw NOT_ENOUGH_EXCEPTION.create(LifestealHelper.getHearts(player));
            }

            for (int i = 0; i < amount; i++) {
                LifestealHelper.removeHeart(player, Optional.empty(), dropItem);
            }

            source.sendFeedback(
                    () -> Text.translatable(
                            "commands.hearts.withdraw.success",
                            amount, LifestealHelper.getHearts(player)
                    ),
                    false
            );

            return amount;
        }

        private static int executeWithdraw(ServerCommandSource source, int amount, boolean dropItem) throws CommandSyntaxException {
            return executeWithdraw(source, source.getPlayerOrThrow(), amount, dropItem);
        }
    }

    public static class GiveNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("give")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> executeGive(context.getSource(), 1))
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                            .executes(context -> executeGive(context.getSource(), IntegerArgumentType.getInteger(context, "amount")))
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .executes(context -> executeGive(context.getSource(), EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))).build();
        }

        private static int executeGive(ServerCommandSource source, ServerPlayerEntity player, int amount) {
            for (int i = 0; i < amount; i++) {
                player.giveItemStack(LifestealHelper.getHeartItem());
            }

            source.sendFeedback(
                    () -> Text.translatable(
                            "commands.hearts.give.success",
                            amount, player.getName()
                    ),
                    false
            );

            return amount;
        }

        private static int executeGive(ServerCommandSource source, int amount) throws CommandSyntaxException {
            return executeGive(source, source.getPlayerOrThrow(), amount);
        }
    }

    public static class AddNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("add")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                            .executes(context -> executeAdd(context.getSource(), IntegerArgumentType.getInteger(context, "amount")))
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .executes(context -> executeAdd(context.getSource(), EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))).build();
        }

        private static int executeAdd(ServerCommandSource source, ServerPlayerEntity player, int amount) {
            LifestealHelper.addHearts(player, amount);

            source.sendFeedback(
                    () -> Text.translatable(
                            "commands.hearts.add.success",
                            amount, player.getName(), LifestealHelper.getHearts(player)
                    ),
                    false
            );

            return amount;
        }

        private static int executeAdd(ServerCommandSource source, int amount) throws CommandSyntaxException {
            return executeAdd(source, source.getPlayerOrThrow(), amount);
        }
    }

    public static class SetNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("set")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                            .executes(context -> executeSet(context.getSource(), IntegerArgumentType.getInteger(context, "amount")))
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .executes(context -> executeSet(context.getSource(), EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))).build();
        }

        private static int executeSet(ServerCommandSource source, ServerPlayerEntity player, int amount) {
            LifestealHelper.setHearts(player, amount);

            source.sendFeedback(
                    () -> Text.translatable(
                            "commands.hearts.set.success",
                            player.getName(), amount
                    ),
                    false
            );

            return amount;
        }

        private static int executeSet(ServerCommandSource source, int amount) throws CommandSyntaxException {
            return executeSet(source, source.getPlayerOrThrow(), amount);
        }
    }

    public static class GetNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("get")
                    .executes(context -> executeGet(context.getSource(), 1.0F))
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("factor", FloatArgumentType.floatArg())
                            .executes(context -> executeGet(context.getSource(), IntegerArgumentType.getInteger(context, "factor"))))
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(context -> executeGet(context.getSource(), EntityArgumentType.getPlayer(context, "player"), 1.0F))
                            .then(CommandManager.argument("factor", FloatArgumentType.floatArg())
                                    .executes(context -> executeGet(context.getSource(), EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "factor"))))).build();
        }

        private static int executeGet(ServerCommandSource source, ServerPlayerEntity player, float factor) {
            int amount = LifestealHelper.getHearts(player);

            source.sendFeedback(
                    () -> Text.translatable(
                            "commands.hearts.get.success",
                            player.getName(), amount
                    ),
                    false
            );

            return Math.round((amount * 1000) * factor);
        }

        private static int executeGet(ServerCommandSource source, float factor) throws CommandSyntaxException {
            return executeGet(source, source.getPlayerOrThrow(), factor);
        }
    }
}
