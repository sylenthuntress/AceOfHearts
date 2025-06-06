package sylenthuntress.aceofhearts.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.aceofhearts.duck.Duck_ShapedRecipeJsonBuilder;
import sylenthuntress.aceofhearts.util.LifestealHelper;

@Mixin(ShapedRecipeJsonBuilder.class)
public class Mixin_ShapedRecipeJsonBuilder implements Duck_ShapedRecipeJsonBuilder {
    @Unique
    private ItemStack outputStack;

    @Override
    public void aceOfHearts$setOutput(ItemStack stack) {
        outputStack = stack;
    }

    @ModifyExpressionValue(
            method = "offerTo",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/item/ItemConvertible;I)Lnet/minecraft/item/ItemStack;"
            )
    )
    public ItemStack replaceOutput(ItemStack original) {
        return outputStack;
    }
}
