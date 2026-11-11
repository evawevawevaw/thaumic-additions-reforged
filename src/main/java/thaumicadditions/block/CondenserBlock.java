package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.wand.WandInteractable;
import thaumicadditions.blockentity.CondenserBlockEntity;
import thaumicadditions.magic.MagicPort;
import thaumicadditions.registry.ModBlockEntities;

public final class CondenserBlock extends Block implements EntityBlock, WandInteractable {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public CondenserBlock(Properties properties) {
        super(properties);
        BlockState state = stateDefinition.any();
        for (var property : InfuserBlock.CONNECTIONS.values()) {
            state = state.setValue(property, false);
        }
        registerDefaultState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        for (var property : InfuserBlock.CONNECTIONS.values()) {
            builder.add(property);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos neighbour = context.getClickedPos().relative(side);
            boolean connected = context.getLevel().isLoaded(neighbour)
                && context.getLevel().getBlockEntity(neighbour) instanceof MagicPort port && port.canConnect(side.getOpposite());
            state = state.setValue(InfuserBlock.CONNECTIONS.get(side), connected);
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction side, BlockState neighbour, LevelAccessor level,
        BlockPos pos, BlockPos neighbourPos) {
        if (side.getAxis().isHorizontal()) {
            boolean connected = level.getBlockEntity(neighbourPos) instanceof MagicPort port && port.canConnect(side.getOpposite());
            return state.setValue(InfuserBlock.CONNECTIONS.get(side), connected);
        }
        return super.updateShape(state, side, neighbour, level, pos, neighbourPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof CondenserBlockEntity machine) {
            server.openMenu(machine, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResult onWandUseOn(Level level, BlockPos pos, Player player, ItemStack wand, Direction side) {
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, side, wand)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CondenserBlockEntity machine) {
            Block.popResource(level, pos.above(), machine.removeUpgrade());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moved) {
        if (!state.is(replacement.getBlock()) && level.getBlockEntity(pos) instanceof CondenserBlockEntity machine) {
            Containers.dropContents(level, pos, machine);
            machine.clearContent();
            Block.popResource(level, pos, machine.removeUpgrade());
            Block.popResource(level, pos, machine.removeUpgrade());
        }
        super.onRemove(state, level, pos, replacement, moved);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CondenserBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != ModBlockEntities.CONDENSER.get() ? null :
            (tickLevel, pos, current, entity) -> {
                if (entity instanceof CondenserBlockEntity machine) {
                    machine.serverTick();
                }
            };
    }
}
