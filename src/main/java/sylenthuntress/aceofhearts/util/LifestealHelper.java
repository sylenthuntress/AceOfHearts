package sylenthuntress.aceofhearts.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import sylenthuntress.aceofhearts.AceOfHearts;
import sylenthuntress.aceofhearts.registry.ModGamerules;

import java.util.Objects;
import java.util.Optional;

public class LifestealHelper {
    private static final GameProfile HEART_PROFILE = new GameProfile(
            Uuids.toUuid(new int[]{1147491811, -462272572, -1927358593, 866243922}),
            ""
    );
    private static final Property HEART_PROPERTY = new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NkYWE1YmYwYmM1NDIwNTBjNzM0YzU4ZjQyODMyMTVjOWE0ZjBmMThjM2RkYWQ4NzE5MTNkYmY2NjdjZTQzMyJ9fX0=");
    private static final Identifier HEALTH_MODIFIER_ID = AceOfHearts.modIdentifier("stolen_hearts");

    static {
        HEART_PROFILE.getProperties().put("skin", HEART_PROPERTY);
    }

    public static ItemEntity spawnHeart(ServerWorld world, BlockPos pos) {
        return new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), getHeartItem());
    }

    public static void removeHeart(ServerPlayerEntity owner, Optional<ServerPlayerEntity> source) {
        ServerWorld serverWorld = owner.getServerWorld();

        addStolenHearts(owner, -2.0);

        if (source.isPresent()) {
            if (Math.ceil(getMaxHealth(source.get())) <= serverWorld.getGameRules().getInt(ModGamerules.MAX_HEALTH)) {
                ItemEntity heartEntity = spawnHeart(serverWorld, owner.getBlockPos());
                heartEntity.setOwner(owner.getUuid());
            } else addStolenHearts(source.get(), 2.0);
        }
    }

    public static double getMaxHealth(LivingEntity entity) {
        return Objects.requireNonNull(entity.getAttributes().getCustomInstance(EntityAttributes.MAX_HEALTH)).getValue();
    }

    public static void addStolenHearts(ServerPlayerEntity player, double amount) {
        EntityAttributeInstance attributeInstance = player.getAttributes().getCustomInstance(EntityAttributes.MAX_HEALTH);
        if (attributeInstance == null) {
            return;
        }

        EntityAttributeModifier existingModifier =  attributeInstance.getModifier(HEALTH_MODIFIER_ID);
        if (existingModifier != null) {
            amount += existingModifier.value();
        }

        attributeInstance.addPersistentModifier(new EntityAttributeModifier(
                HEALTH_MODIFIER_ID,
                amount,
                EntityAttributeModifier.Operation.ADD_VALUE
        ));
    }

    public static ItemStack getHeartItem() {
        ItemStack heartStack = new ItemStack(Items.PLAYER_HEAD);
        heartStack.set(DataComponentTypes.PROFILE, new ProfileComponent(HEART_PROFILE));

        return heartStack;
    }
}
