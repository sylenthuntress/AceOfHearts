package sylenthuntress.aceofhearts.event;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisallowMaceEnchantments implements EnchantmentEvents.AllowEnchanting {
    private static final Map<RegistryKey<Enchantment>, List<Item>> disallowedEnchantments = new HashMap<>();

    static {
        disallowedEnchantments.put(Enchantments.BREACH, List.of(Items.MACE));
        disallowedEnchantments.put(Enchantments.DENSITY, List.of(Items.MACE));
    }

    @Override
    public TriState allowEnchanting(RegistryEntry<Enchantment> entry, ItemStack stack, EnchantingContext contextD) {
        if (entry.matches(key -> disallowedEnchantments.getOrDefault(key, List.of()).contains(stack.getItem()))) {
            return TriState.FALSE;
        }

        return TriState.DEFAULT;
    }
}
