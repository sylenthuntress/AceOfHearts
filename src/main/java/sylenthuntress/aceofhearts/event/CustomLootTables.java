package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;
import sylenthuntress.aceofhearts.duck.Duck_LootTable;

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
        Collection<Item> disallowedItems = new ArrayList<>();

        if (EntityType.EVOKER.getLootTableKey().isPresent() && EntityType.EVOKER.getLootTableKey().get() == key) {
            disallowedItems.add(Items.TOTEM_OF_UNDYING);
        }

        for (Item item : disallowedItems) {
            ((Duck_LootTable) original).aceofhearts$addDisallowedItems(item);
        }

        return original;
    }
}
