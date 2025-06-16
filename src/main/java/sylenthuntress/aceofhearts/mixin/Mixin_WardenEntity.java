package sylenthuntress.aceofhearts.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
public abstract class Mixin_WardenEntity extends LivingEntity {
    @Shadow
    @Final
    private static TrackedData<Integer> ANGER;

    @Shadow
    private WardenAngerManager angerManager;

    protected Mixin_WardenEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract Angriness getAngriness();

    @Inject(method = "damage", at = @At("HEAD"))
    public void breakBlocks(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        breakBlocks(world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void breakBlocks(CallbackInfo ci) {
        if (this.getAngriness().isAngry()) {
            breakBlocks(this.getWorld());
        }
    }

    @Unique
    private void breakBlocks(World world) {
        Box box = this.getBoundingBox().offset(0, 1, 0).expand(1);

        for (BlockPos blockPos : BlockPos.iterate(
                MathHelper.floor(box.minX),
                MathHelper.floor(box.minY),
                MathHelper.floor(box.minZ),
                MathHelper.floor(box.maxX),
                MathHelper.floor(box.maxY),
                MathHelper.floor(box.maxZ)
        )) {
            if (this.getRandom().nextBetween(0, 3) == 0) {
                continue;
            }

            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isIn(BlockTags.SCULK_REPLACEABLE) || blockState.isIn(BlockTags.WOOL)) {
                world.breakBlock(blockPos, true, this);
            }
        }
    }
}
