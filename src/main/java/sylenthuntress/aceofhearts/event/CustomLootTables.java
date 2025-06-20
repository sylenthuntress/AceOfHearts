package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class CustomLootTables implements LootTableEvents.Modify, LootTableEvents.Replace {
    @Override
    public void modifyLootTable(RegistryKey<LootTable> key, LootTable.Builder builder, LootTableSource source, RegistryWrapper.WrapperLookup wrapperLookup) {
        Collection<LootPool> pools = new ArrayList<>();

        if (EntityType.WARDEN.getLootTableKey().isPresent() && EntityType.WARDEN.getLootTableKey().get() == key) {
            pools.add(LootPool
                    .builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .conditionally(KilledByPlayerLootCondition.builder())
                    .with(ItemEntry.builder(Items.TOTEM_OF_UNDYING)).build()
            );
        }

        builder.pools(pools);
    }

    @Override
    public @Nullable LootTable replaceLootTable(RegistryKey<LootTable> key, LootTable original, LootTableSource source, RegistryWrapper.WrapperLookup registries) {
        if (EntityType.EVOKER.getLootTableKey().isPresent() && EntityType.EVOKER.getLootTableKey().get() == key) {
            return LootTable.builder()
                    .pool(
                            LootPool.builder()
                                    .rolls(ConstantLootNumberProvider.create(1.0F))
                                    .with(
                                            ItemEntry.builder(Items.EMERALD)
                                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
                                                    .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0.0F, 1.0F)))
                                    )
                                    .conditionally(KilledByPlayerLootCondition.builder())
                    ).build();
        }

        return original;
    }
}
