package sylenthuntress.aceofhearts.mixin;

import com.hypherionmc.sdlink.api.accounts.DiscordAuthor;
import com.hypherionmc.sdlink.core.config.AvatarType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(DiscordAuthor.class)
public class Mixin_DiscordAuthor {
    @WrapOperation(
            method = "of(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/hypherionmc/sdlink/api/accounts/DiscordAuthor;",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hypherionmc/sdlink/core/config/AvatarType;resolve(Ljava/lang/String;)Ljava/lang/String;",
                    remap = false
            )
    )
    private static String geyserCompat(AvatarType instance, String uuid, Operation<String> original, String displayName, String username) {
        if (username.startsWith(".")) {
            uuid = username;
        }

        return original.call(instance, uuid);
    }
}
