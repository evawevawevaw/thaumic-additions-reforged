package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.blockentity.CreativeNodeBlockEntity;
import thaumicadditions.registry.ModBlockEntities;

public final class CreativeNodeBlock extends Block implements EntityBlock {
    public CreativeNodeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeNodeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type != ModBlockEntities.CREATIVE_NODE.get() ? null : (tickLevel, pos, current, entity) -> {
            if (entity instanceof CreativeNodeBlockEntity node) {
                node.tick();
            }
        };
    }
}
