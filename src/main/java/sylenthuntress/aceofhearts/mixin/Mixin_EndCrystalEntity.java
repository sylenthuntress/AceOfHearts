package sylenthuntress.aceofhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndCrystalEntity.class)
public class Mixin_EndCrystalEntity {
    @Unique
    private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
        @Override
        public boolean shouldDamage(Explosion explosion, Entity entity) {
            return false;
        }

        @Override
        public float getKnockbackModifier(Entity entity) {
            return 10.0F;
        }
    };

    @WrapOperation(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)V"
            )
    )
    public void s(ServerWorld instance, Entity entity, DamageSource damageSource, ExplosionBehavior explosionBehavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType sourceType, Operation<Void> original) {
        original.call(instance, entity, damageSource, EXPLOSION_BEHAVIOR, x, y, z, power, createFire, sourceType);
    }
}
