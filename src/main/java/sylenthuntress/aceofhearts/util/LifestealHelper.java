package sylenthuntress.aceofhearts.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.PlaySoundConsumeEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import sylenthuntress.aceofhearts.AceOfHearts;
import sylenthuntress.aceofhearts.registry.ModAttachmentTypes;
import sylenthuntress.aceofhearts.registry.ModGamerules;

import java.util.List;
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

    public static void removeHeart(ServerPlayerEntity owner, Optional<ServerPlayerEntity> source) {
        ServerWorld serverWorld = owner.getServerWorld();

        addHeart(owner, -1);

        if (source.isEmpty() || getHearts(source.get()) <= serverWorld.getGameRules().getInt(ModGamerules.MAX_HEARTS)) {
            owner.dropItem(getHeartItem(), true, true);
        } else source.ifPresent(serverPlayer -> addHeart(serverPlayer, 1));
    }

    @SuppressWarnings({"UnstableApiUsage"})
    public static double getHearts(ServerPlayerEntity player) {
        return player.getAttachedOrElse(ModAttachmentTypes.HEARTS, 10);
    }

    public static void recalculateHealth(ServerPlayerEntity player) {
        EntityAttributeInstance attributeInstance = player.getAttributes().getCustomInstance(EntityAttributes.MAX_HEALTH);
        if (attributeInstance == null) {
            return;
        }

        double newHealth = -attributeInstance.getBaseValue() + getHearts(player) * 2.0;
        if (newHealth < -18.0) {
            newHealth = -18.0;
        }

        attributeInstance.removeModifier(HEALTH_MODIFIER_ID);
        attributeInstance.addPersistentModifier(new EntityAttributeModifier(
                HEALTH_MODIFIER_ID,
                newHealth,
                EntityAttributeModifier.Operation.ADD_VALUE
        ));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void addHeart(ServerPlayerEntity player, int amount) {
        if (!player.hasAttached(ModAttachmentTypes.HEARTS)) {
            player.setAttached(ModAttachmentTypes.HEARTS, 10);
        }

        player.modifyAttached(ModAttachmentTypes.HEARTS, hearts -> hearts + amount);
        recalculateHealth(player);
    }

    public static ItemStack getHeartItem() {
        ItemStack heartStack = new ItemStack(Items.PLAYER_HEAD);

        // Appearance
        heartStack.set(DataComponentTypes.PROFILE, new ProfileComponent(HEART_PROFILE));

        // Behavior
        heartStack.remove(DataComponentTypes.EQUIPPABLE);

        // Flavor text
        heartStack.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("item.heart.name"));
        heartStack.set(DataComponentTypes.LORE, new LoreComponent(
                List.of(
                        Text.translatable("item.heart.desc.1"),
                        Text.translatable("item.heart.desc.2")
                )
        ));

        return heartStack;
    }
}
