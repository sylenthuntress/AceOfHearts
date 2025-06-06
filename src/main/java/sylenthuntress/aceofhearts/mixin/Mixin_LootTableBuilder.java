package sylenthuntress.aceofhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import sylenthuntress.aceofhearts.duck.Duck_LootTable;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(LootTable.Builder.class)
public class Mixin_LootTableBuilder implements Duck_LootTable {
    @Unique
    private final Collection<Item> disallowedItems = new ArrayList<>();

    @WrapMethod(method = "build")
    public LootTable transferDisallowedItems(Operation<LootTable> original) {
        LootTable table = original.call();

        for (Item item : disallowedItems) {
            ((Duck_LootTable)table).aceofhearts$addDisallowedItems(item);
        }

        return table;
    }

    @Override
    public void aceofhearts$addDisallowedItems(Item item) {
        disallowedItems.add(item);
    }
}
