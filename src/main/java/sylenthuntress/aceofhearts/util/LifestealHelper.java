package sylenthuntress.aceofhearts.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
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

    static {
        HEART_PROFILE.getProperties().put("skin", HEART_PROPERTY);
    }

    private static final ProfileComponent PROFILE_COMPONENT = new ProfileComponent(HEART_PROFILE);

    private static final Identifier HEALTH_MODIFIER_ID = AceOfHearts.modIdentifier("stolen_hearts");

    public static void removeHeart(PlayerEntity owner, Optional<? extends PlayerEntity> source, boolean dropItem) {
        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 6, 0, false, false));
        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 6, 0, false, false));
        addHearts(owner, -1);

        if (source.isEmpty() || hasMaxHearts(source.get())) {
            ItemStack heartItem = getHeartItem();
            LoreComponent lore = heartItem.get(DataComponentTypes.LORE);
            if (lore != null) {
                lore = lore.with(Text.translatable("item.heart.desc.player", owner.getName()).formatted(Formatting.UNDERLINE, Formatting.DARK_GRAY));
                heartItem.set(DataComponentTypes.LORE, lore);
            }

            if (dropItem) {
                owner.dropItem(heartItem, true, true);
            } else owner.giveItemStack(heartItem);
        } else source.ifPresent(serverPlayer -> addHearts(serverPlayer, 1));
    }

    @SuppressWarnings({"UnstableApiUsage"})
    public static int getHearts(PlayerEntity player) {
        return player.getAttachedOrElse(ModAttachmentTypes.HEARTS, 10);
    }

    public static void recalculateHealth(PlayerEntity player) {
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

    public static void addHearts(PlayerEntity player, int amount) {
        setHearts(player, getHearts(player) + amount);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void setHearts(PlayerEntity player, int amount) {
        player.setAttached(ModAttachmentTypes.HEARTS, Math.clamp(amount, 0, getMaxHearts(player.getWorld())));
        recalculateHealth(player);
    }

    public static ItemStack getHeartItem() {
        ItemStack heartStack = new ItemStack(Items.POISONOUS_POTATO);
        heartStack.set(DataComponentTypes.CUSTOM_DATA, getHeartNbt());

        // Appearance
        heartStack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(Items.PLAYER_HEAD.toString()));
        heartStack.set(DataComponentTypes.PROFILE, PROFILE_COMPONENT);

        // Behavior
        heartStack.set(DataComponentTypes.MAX_STACK_SIZE, 1);
        heartStack.set(DataComponentTypes.CONSUMABLE, ConsumableComponent.builder()
                .consumeSeconds(2.5F)
                .sound(Registries.SOUND_EVENT.getEntry(SoundEvents.ENTITY_WARDEN_HEARTBEAT))
                .finishSound(Registries.SOUND_EVENT.getEntry(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE))
                .build()
        );

        heartStack.set(DataComponentTypes.FOOD, new FoodComponent.Builder()
                .alwaysEdible()
                .nutrition(20)
                .saturationModifier(1)
                .build()
        );

        // Flavor text
        heartStack.set(DataComponentTypes.RARITY, Rarity.EPIC);
        heartStack.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.heart.name"));
        heartStack.set(DataComponentTypes.LORE, new LoreComponent(
                List.of(
                        Text.translatable("item.heart.desc.1").formatted(Formatting.GRAY, Formatting.ITALIC),
                        Text.translatable("item.heart.desc.2").formatted(Formatting.GOLD)
                )
        ));

        return heartStack;
    }

    public static NbtComponent getHeartNbt() {
        var nbt = new NbtCompound();
        nbt.putBoolean("HeartItem", true);
        return NbtComponent.of(nbt);
    }

    public static NbtComponent getTotemNbt() {
        var nbt = new NbtCompound();
        nbt.putBoolean("RevivalTotem", true);
        return NbtComponent.of(nbt);
    }

    public static ItemStack getRevivalTotem() {
        ItemStack revivalTotem = new ItemStack(Items.TOTEM_OF_UNDYING);
        revivalTotem.set(DataComponentTypes.CUSTOM_DATA, getTotemNbt());

        // Flavor text
        revivalTotem.set(DataComponentTypes.RARITY, Rarity.EPIC);
        revivalTotem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        revivalTotem.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.revival_totem.name"));
        revivalTotem.set(DataComponentTypes.LORE, new LoreComponent(
                List.of(
                        Text.translatable("item.revival_totem.desc.1").formatted(Formatting.GRAY, Formatting.ITALIC),
                        Text.translatable("item.revival_totem.desc.2").formatted(Formatting.GOLD)
                )
        ));

        return revivalTotem;
    }

    public static boolean isHeartItem(ItemStack stack) {
        var component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) {
            return false;
        }

        //noinspection deprecation
        var compound = component.getNbt();
        if (compound == null) {
            return false;
        }

        return compound.getBoolean("HeartItem", false);
    }

    public static boolean isRevivalTotem(ItemStack stack) {
        if (stack.getItem() != Items.TOTEM_OF_UNDYING) {
            return false;
        }

        var component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) {
            return false;
        }

        //noinspection deprecation
        var compound = component.getNbt();
        if (compound == null) {
            return false;
        }

        return compound.getBoolean("RevivalTotem", false);
    }

    public static boolean hasMaxHearts(PlayerEntity player) {
        return getHearts(player) >= getMaxHearts(player.getWorld());
    }

    public static int getMaxHearts(World world) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return 20;
        }

        return serverWorld.getGameRules().getInt(ModGamerules.MAX_HEARTS);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static boolean isDead(Entity entity) {
        return entity.getAttachedOrElse(ModAttachmentTypes.DEAD, false);
    }
}
