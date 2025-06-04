package sylenthuntress.aceofhearts.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTable.Builder.class)
public interface Access_LootTableBuilder {
    @Accessor
    ImmutableList.Builder<LootPool> getPools();
}
