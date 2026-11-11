package thaumicadditions.seal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;

public record SealData(int color, List<String> aspects) {
    public static final SealData EMPTY = new SealData(0xFFFFFF, List.of());
    public static final Codec<SealData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.intRange(0, 0xFFFFFF).optionalFieldOf("color", 0xFFFFFF).forGetter(SealData::color),
        Codec.STRING.listOf(0, 3).optionalFieldOf("aspects", List.of()).forGetter(SealData::aspects)
    ).apply(instance, SealData::new));

    public SealData {
        color &= 0xFFFFFF;
        aspects = List.copyOf(aspects.subList(0, Math.min(3, aspects.size())));
    }

    public SealData append(String aspect) {
        if (aspects.size() == 3) {
            return this;
        }
        List<String> updated = new ArrayList<>(aspects);
        updated.add(aspect);
        return new SealData(color, updated);
    }

    public SealData removeLast() {
        return aspects.isEmpty() ? this : new SealData(color, aspects.subList(0, aspects.size() - 1));
    }
}
