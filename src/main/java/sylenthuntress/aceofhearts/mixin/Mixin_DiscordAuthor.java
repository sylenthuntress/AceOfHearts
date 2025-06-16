package sylenthuntress.aceofhearts.mixin;

import com.hypherionmc.sdlink.api.accounts.DiscordAuthor;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(DiscordAuthor.class)
public class Mixin_DiscordAuthor {
    @WrapMethod(method = "of(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/hypherionmc/sdlink/api/accounts/DiscordAuthor;")
    private static DiscordAuthor geyserCompat(String displayName, String uuid, String username, Operation<DiscordAuthor> original) {
        if (username.startsWith(".")) {
            uuid = "." + username;
        }

        return original.call(displayName, uuid, username);
    }
}
