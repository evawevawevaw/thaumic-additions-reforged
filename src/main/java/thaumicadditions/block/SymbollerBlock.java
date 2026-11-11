package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumicadditions.blockentity.SymbollerBlockEntity;
import thaumicadditions.registry.ModBlockEntities;

public final class SymbollerBlock extends Block implements EntityBlock {
    public static final BooleanProperty HAS_JAR = BooleanProperty.create("has_jar");
    private static final VoxelShape SHAPE = net.minecraft.world.phys.shapes.Shapes.or(
        Block.box(0, 0, 0, 16, 3, 16), Block.box(0, 13, 0, 16, 17, 16),
        Block.box(0, 3, 0, 1, 32, 1), Block.box(0, 3, 15, 1, 32, 16),
        Block.box(15, 3, 0, 16, 32, 1), Block.box(15, 3, 15, 16, 32, 16));

    public SymbollerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HAS_JAR, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_JAR);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SymbollerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != ModBlockEntities.SYMBOLLER.get() ? null : (world, pos, current, entity) -> {
            if (entity instanceof SymbollerBlockEntity symboller) {
                symboller.serverTick();
            }
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
        Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof SymbollerBlockEntity symboller && symboller.isEmpty()
            && symboller.canPlaceItem(0, stack)) {
            if (!level.isClientSide) {
                symboller.setItem(0, stack.copy());
                if (!player.isCreative()) {
                    stack.setCount(0);
                }
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof SymbollerBlockEntity symboller && !symboller.isEmpty()) {
            if (!level.isClientSide) {
                ItemStack result = symboller.removeItem(0, symboller.getMaxStackSize());
                if (!player.getInventory().add(result)) {
                    Block.popResource(level, pos, result);
                }
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moved) {
        if (state.getBlock() != replacement.getBlock() && level.getBlockEntity(pos) instanceof SymbollerBlockEntity symboller) {
            Containers.dropContents(level, pos, symboller);
            symboller.clearContent();
        }
        super.onRemove(state, level, pos, replacement, moved);
    }
}
