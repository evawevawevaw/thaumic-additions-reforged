package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumicadditions.blockentity.MagicBufferBlockEntity;
import thaumicadditions.blockentity.MagicTankBlockEntity;
import thaumicadditions.blockentity.OldCrucibleBlockEntity;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.registry.ModBlockEntities;

public final class MagicStorageBlock extends Block implements EntityBlock {
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    private static final VoxelShape TANK = Block.box(1, 0, 1, 15, 16, 15);
    private static final VoxelShape CRUCIBLE = Shapes.or(Block.box(0, 0, 0, 16, 5, 16),
        Block.box(0, 5, 0, 2, 16, 16), Block.box(14, 5, 0, 16, 16, 16),
        Block.box(2, 5, 0, 14, 16, 2), Block.box(2, 5, 14, 14, 16, 16));
    private final boolean reinforced;
    private final boolean crucible;

    public MagicStorageBlock(Properties properties, boolean reinforced, boolean crucible) {
        super(properties);
        this.reinforced = reinforced;
        this.crucible = crucible;
        registerDefaultState(stateDefinition.any().setValue(TOP, false).setValue(BOTTOM, false));
    }

    public boolean reinforced() {
        return reinforced;
    }

    public boolean crucible() {
        return crucible;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        return defaultBlockState().setValue(TOP, isTank(context.getLevel().getBlockState(pos.above())))
            .setValue(BOTTOM, isTank(context.getLevel().getBlockState(pos.below())));
    }

    private static boolean isTank(BlockState state) {
        return state.getBlock() instanceof MagicStorageBlock block && !block.crucible;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction side, BlockState neighbour, LevelAccessor level,
        BlockPos pos, BlockPos neighbourPos) {
        if (!crucible && side == Direction.UP) {
            return state.setValue(TOP, isTank(neighbour));
        }
        if (!crucible && side == Direction.DOWN) {
            return state.setValue(BOTTOM, isTank(neighbour));
        }
        return super.updateShape(state, side, neighbour, level, pos, neighbourPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return crucible ? CRUCIBLE : TANK;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MagicBufferBlockEntity buffer) {
            player.displayClientMessage(Component.translatable("message.thaumicadditions.magic_storage",
                buffer.stored(MagicKind.VIS), buffer.stored(MagicKind.TAINT), buffer.capacity()), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (crucible) {
            for (int i = random.nextInt(4); i > 0; i--) {
                thaumicadditions.fx.ColoredFlames.spawn(level,
                    pos.getX() + 0.5 + (random.nextDouble() - random.nextDouble()) / 2,
                    pos.getY() + 0.0625, pos.getZ() + 0.5 + (random.nextDouble() - random.nextDouble()) / 2,
                    21760, 40, 1);
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return crucible ? new OldCrucibleBlockEntity(pos, state) : new MagicTankBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != (crucible ? ModBlockEntities.OLD_CRUCIBLE.get() : ModBlockEntities.MAGIC_TANK.get())) {
            return null;
        }
        return (tickLevel, pos, current, entity) -> {
            if (entity instanceof MagicBufferBlockEntity buffer) {
                buffer.serverTick();
            }
        };
    }
}
