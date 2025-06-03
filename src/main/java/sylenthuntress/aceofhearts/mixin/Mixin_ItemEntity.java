package sylenthuntress.aceofhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.ParseResults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.aceofhearts.util.LifestealHelper;
import sylenthuntress.aceofhearts.util.TeamBuilder;

@Mixin(ItemEntity.class)
public abstract class Mixin_ItemEntity extends Entity {
    public Mixin_ItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getStack();

    @Shadow public abstract void writeCustomDataToNbt(NbtCompound nbt);

    @Shadow public abstract @Nullable Entity teleportTo(TeleportTarget teleportTarget);

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    public void preventVoidDespawn(CallbackInfo ci) {
        World world = this.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        if (this.getY() >= world.getDimension().minY()) {
            return;
        }

        setRedGlow();
        serverWorld.getServer().getCommandManager().executeWithPrefix(
                this.getCommandSource(serverWorld).withLevel(2),
                "/execute in minecraft:the_end run spreadplayers 0 0 1 100 false @s"
        );
    }

    @WrapOperation(
            method = {
                    "tick",
                    "damage"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;discard()V"
            )
    )
    public void preventDespawn(ItemEntity instance, Operation<Void> original) {
        if (LifestealHelper.isHeartItem(this.getStack())) {
            setRedGlow();
            return;
        }

        original.call(instance);

    }

    @Unique
    private void setRedGlow() {
        TeamBuilder.create("heart_item")
                .setColor(Formatting.RED)
                .addMembers(this)
                .build(this.getWorld().getScoreboard());
        this.setGlowing(true);
    }
}
