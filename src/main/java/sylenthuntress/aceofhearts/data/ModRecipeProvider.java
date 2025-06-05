package sylenthuntress.aceofhearts.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.ComponentsIngredient;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import sylenthuntress.aceofhearts.AceOfHearts;
import sylenthuntress.aceofhearts.duck.Duck_ShapedRecipeJsonBuilder;
import sylenthuntress.aceofhearts.util.LifestealHelper;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
                var revivalTotemBuilder = createShaped(RecipeCategory.MISC, Items.TOTEM_OF_UNDYING)
                        .pattern("hth")
                        .pattern("hhh")
                        .pattern(" h ")
                        .input('h', DefaultCustomIngredients.components(
                                Ingredient.ofItem(Items.POISONOUS_POTATO),
                                builder -> builder.add(DataComponentTypes.CUSTOM_DATA, LifestealHelper.getHeartNbt()))
                        )
                        .input('t', Items.TOTEM_OF_UNDYING)
                        .criterion(hasItem(Items.TOTEM_OF_UNDYING), conditionsFromItem(Items.TOTEM_OF_UNDYING))
                        .showNotification(true);
                ((Duck_ShapedRecipeJsonBuilder)revivalTotemBuilder).aceOfHearts$setOutput(LifestealHelper.getRevivalTotem());
                revivalTotemBuilder.offerTo(exporter, AceOfHearts.MOD_ID+":revival_totem");
            }
        };
    }

    @Override
    public String getName() {
        return AceOfHearts.MOD_NAME+"RecipeProvider";
    }
}
