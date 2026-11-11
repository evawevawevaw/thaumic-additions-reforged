package thaumicadditions.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.wand.WandInteractable;
import thaumicadditions.blockentity.VisPipeBlockEntity;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicPort;
import thaumicadditions.magic.PipeNetwork;
import thaumicadditions.registry.ModBlockEntities;

public final class VisPipeBlock extends Block implements EntityBlock, WandInteractable {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final Map<Direction, BooleanProperty> CONNECTIONS = PipeBlock.PROPERTY_BY_DIRECTION;
    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final Map<Direction, VoxelShape> ARMS = Map.of(
        Direction.DOWN, Block.box(6, 0, 6, 10, 8, 10), Direction.UP, Block.box(6, 8, 6, 10, 16, 10),
        Direction.NORTH, Block.box(6, 6, 0, 10, 10, 8), Direction.SOUTH, Block.box(6, 6, 8, 10, 10, 16),
        Direction.WEST, Block.box(0, 6, 6, 8, 10, 10), Direction.EAST, Block.box(8, 6, 6, 16, 10, 10));
    private final boolean filter;

    public VisPipeBlock(Properties properties, boolean filter) {
        super(properties);
        this.filter = filter;
        BlockState state = stateDefinition.any().setValue(AXIS, Direction.Axis.Y);
        for (BooleanProperty property : CONNECTIONS.values()) {
            state = state.setValue(property, false);
        }
        registerDefaultState(state);
    }

    public boolean filter() {
        return filter;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (filter || !(level.getBlockEntity(pos) instanceof VisPipeBlockEntity pipe)) {
            return;
        }
        int vis = pipe.stored(MagicKind.VIS);
        int taint = pipe.stored(MagicKind.TAINT);
        int total = vis + taint;
        if (total < PipeNetwork.CAPACITY - 1) {
            return;
        }
        int color = 0;
        for (int shift : new int[]{0, 8, 16}) {
            int channel = ((MagicKind.VIS.color() >> shift & 255) * vis + (MagicKind.TAINT.color() >> shift & 255) * taint) / total;
            color |= channel << shift;
        }
        thaumicadditions.fx.VisDrips.spawn(level,
            pos.getX() + 0.5 + (random.nextGaussian() - random.nextGaussian()) / 8,
            pos.getY() + 0.375,
            pos.getZ() + 0.5 + (random.nextGaussian() - random.nextGaussian()) / 8,
            color, 0.5F + random.nextFloat() / 5);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
        for (BooleanProperty property : CONNECTIONS.values()) {
            builder.add(property);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moved) {
        super.onPlace(state, level, pos, previous, moved);
        if (!level.isClientSide) {
            PipeNetwork.invalidate(level);
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction side, BlockState neighbour, LevelAccessor level,
        BlockPos pos, BlockPos neighbourPos) {
        level.scheduleTick(pos, this, 1);
        if (level instanceof Level world && !world.isClientSide) {
            PipeNetwork.invalidate(world);
        }
        return super.updateShape(state, side, neighbour, level, pos, neighbourPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof VisPipeBlockEntity pipe)) {
            return;
        }
        BlockState updated = state;
        for (Direction side : Direction.values()) {
            BlockPos neighbour = pos.relative(side);
            boolean connected = pipe.canConnect(side) && level.isLoaded(neighbour)
                && level.getBlockEntity(neighbour) instanceof MagicPort port && port.canConnect(side.getOpposite());
            updated = updated.setValue(CONNECTIONS.get(side), connected);
        }
        if (updated != state) {
            level.setBlock(pos, updated, 3);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (filter) {
            return Shapes.block();
        }
        VoxelShape shape = CORE;
        for (Direction side : Direction.values()) {
            if (state.getValue(CONNECTIONS.get(side))) {
                shape = Shapes.or(shape, ARMS.get(side));
            }
        }
        return shape;
    }

    @Override
    public InteractionResult onWandUseOn(Level level, BlockPos pos, Player player, ItemStack wand, Direction side) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof VisPipeBlockEntity pipe) {
            pipe.toggleSide(side);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof VisPipeBlockEntity pipe) {
            player.displayClientMessage(Component.translatable("message.thaumicadditions.magic_storage",
                pipe.stored(MagicKind.VIS), pipe.stored(MagicKind.TAINT), pipe.capacity()), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VisPipeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != ModBlockEntities.VIS_PIPE.get() ? null :
            (tickLevel, pos, current, entity) -> {
                if (entity instanceof VisPipeBlockEntity pipe) {
                    pipe.serverTick();
                }
            };
    }
}
