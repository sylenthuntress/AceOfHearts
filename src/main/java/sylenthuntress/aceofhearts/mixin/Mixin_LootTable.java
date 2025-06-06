package sylenthuntress.aceofhearts.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.aceofhearts.duck.Duck_LootTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(LootTable.class)
public class Mixin_LootTable implements Duck_LootTable {
    @Unique
    private final Collection<Item> disallowedItems = new ArrayList<>();

    @ModifyExpressionValue(
            method = "generateUnprocessedLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/loot/LootTable;pools:Ljava/util/List;"
            )
    )
    public List<LootPool> filterDisallowedItems(List<LootPool> original) {
        return original.stream().map(lootPool -> {
            ImmutableList.Builder<LootPoolEntry> entries = new ImmutableList.Builder<>();
            for (LootPoolEntry entry : lootPool.entries) {
               if (entry instanceof ItemEntry itemEntry) {
                   //noinspection deprecation
                   if (disallowedItems.stream().anyMatch(item -> item.getRegistryEntry().matches(((Access_ItemEntry)itemEntry).getItem()))) {
                       break;
                   }
               }

               if (entry instanceof TagEntry tagEntry) {
                   //noinspection deprecation
                   if (disallowedItems.stream().anyMatch(item -> item.getRegistryEntry().isIn(((Access_TagEntry)tagEntry).getName()))) {
                       break;
                   }
               }

               entries.add(entry);
           }

            LootPool.Builder poolBuilder = new LootPool.Builder();
            for (LootPoolEntry entry : entries.build()) {
                poolBuilder.with(entry);
            }

            poolBuilder.conditionally(lootPool.conditions);
            poolBuilder.rolls(lootPool.rolls);
            poolBuilder.bonusRolls(lootPool.bonusRolls);

            return poolBuilder.build();
        }).toList();
    }

    @Override
    public void aceofhearts$addDisallowedItems(Item item) {
        disallowedItems.add(item);
    }
}
