package sylenthuntress.aceofhearts.mixin;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SmithingScreenHandler.class)
public abstract class Mixin_SmithingScreenHandler extends ForgingScreenHandler {
    @Shadow @Final public static int TEMPLATE_ID;

    @Shadow @Final public static int EQUIPMENT_ID;

    public Mixin_SmithingScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Inject(
            method = "updateResult",
            at = @At("TAIL")
    )
    private void replaceNetherite(CallbackInfo ci) {
        if (this.input.getStack(TEMPLATE_ID).getItem() == Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
            ItemStack inputStack = this.input.getStack(0);
            ItemStack outputStack = this.output.getStack(0);

            copyComponent(DataComponentTypes.ATTRIBUTE_MODIFIERS, outputStack, inputStack);
            copyComponent(DataComponentTypes.DAMAGE_RESISTANT, outputStack, inputStack);

            outputStack.set(DataComponentTypes.LORE,
                    new LoreComponent(
                            List.of(Text.translatable("item.netherite.equal_diamond")
                                    .formatted(Formatting.GRAY)
                            )
                    )
            );
        }
    }

    @Unique
    private static <T> void copyComponent(ComponentType<T> componentType, ItemStack stack, ItemStack sourceStack) {
        stack.set(componentType, sourceStack.get(componentType));
    }
}
