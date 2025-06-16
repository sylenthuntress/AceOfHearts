package sylenthuntress.aceofhearts.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import sylenthuntress.aceofhearts.AceOfHearts;

@SuppressWarnings("UnstableApiUsage")
public class ModAttachmentTypes {
    public static final AttachmentType<Integer> HEARTS = AttachmentRegistry.create(
            AceOfHearts.modIdentifier("hearts"),
            builder -> builder
                    .initializer(() -> 10)
                    .persistent(Codec.INT)
    );

    public static final AttachmentType<Boolean> DEAD = AttachmentRegistry.create(
            AceOfHearts.modIdentifier("is_dead"),
            builder -> builder
                    .initializer(() -> false)
                    .persistent(Codec.BOOL)
    );

    public static final AttachmentType<Integer> GRACE_PERIOD = AttachmentRegistry.create(
            AceOfHearts.modIdentifier("grace_period"),
            builder -> builder
                    .initializer(() -> -1)
                    .persistent(Codec.INT)
    );

    public static void registerAll() {

    }
}
