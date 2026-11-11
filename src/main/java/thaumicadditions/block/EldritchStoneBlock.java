package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class EldritchStoneBlock extends Block {
    public EldritchStoneBlock(Properties properties) {
        super(properties);
        BlockState state = stateDefinition.any();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            state = state.setValue(property(direction), false);
        }
        registerDefaultState(state);
    }

    public static BooleanProperty property(Direction direction) {
        return PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            builder.add(property(direction));
        }
    }

    public static BlockState connected(BlockState state, BlockGetter level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            state = state.setValue(property(direction), level.getBlockState(pos.relative(direction)).getBlock() instanceof EldritchStoneBlock);
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return connected(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level,
        BlockPos pos, BlockPos neighborPos) {
        return direction.getAxis().isHorizontal()
            ? state.setValue(property(direction), neighbor.getBlock() instanceof EldritchStoneBlock) : state;
    }
}
