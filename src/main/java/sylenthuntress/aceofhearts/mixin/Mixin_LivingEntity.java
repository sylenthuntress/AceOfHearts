package sylenthuntress.aceofhearts.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.aceofhearts.util.LifestealHelper;

@Mixin(LivingEntity.class)
public abstract class Mixin_LivingEntity {
    @Shadow public abstract Hand getActiveHand();

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @ModifyReturnValue(
            method = "canGlide",
            at = @At("RETURN")
    )
    public boolean disableElytra(boolean original) {
        return false;
    }

    @Inject(
            method = "consumeItem",
            at = @At("HEAD"),
            cancellable = true
    )
    public void consumeHeart(CallbackInfo ci) {
        if (!((LivingEntity) (Object) this instanceof ServerPlayerEntity player)) {
            return;
        }

        ItemStack stack = this.getStackInHand(this.getActiveHand());
        if (!LifestealHelper.isHeartItem(stack)) {
            return;
        }

        if (LifestealHelper.hasMaxHearts(player)) {
            ci.cancel();

            player.sendMessage(
                    Text.translatable(
                            "item.heart.use.max_hearts",
                            Text.translatable("item.heart.use.add_heart.count", LifestealHelper.getMaxHearts(player.getServerWorld()))
                                    .formatted(Formatting.RED)
                    ).formatted(Formatting.DARK_GRAY),
                    true
            );
            player.playSoundToPlayer(
                    SoundEvents.ENTITY_WITHER_BREAK_BLOCK,
                    SoundCategory.PLAYERS,
                    0.5F,
                    0.5F
            );

            return;
        }

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 120, 0, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 120, 0, false, false));
        LifestealHelper.addHearts(player, 1);
        player.sendMessage(
                Text.translatable(
                        "item.heart.use.add_heart",
                        Text.translatable("item.heart.use.add_heart.count", LifestealHelper.getHearts(player))
                                .formatted(Formatting.RED)
                ).formatted(Formatting.GOLD),
                true
        );
    }
}
