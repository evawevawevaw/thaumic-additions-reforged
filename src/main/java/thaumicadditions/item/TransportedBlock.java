package thaumicadditions.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;

public record TransportedBlock(BlockState state, CustomData blockEntity) {
    public static final Codec<TransportedBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockState.CODEC.fieldOf("state").forGetter(TransportedBlock::state),
        CustomData.CODEC.optionalFieldOf("block_entity", CustomData.EMPTY).forGetter(TransportedBlock::blockEntity)
    ).apply(instance, TransportedBlock::new));
}
