package thaumicadditions.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record RelicOwner(UUID id, String name) {
    public static final Codec<RelicOwner> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("id").forGetter(RelicOwner::id),
        Codec.STRING.fieldOf("name").forGetter(RelicOwner::name)
    ).apply(instance, RelicOwner::new));
}
