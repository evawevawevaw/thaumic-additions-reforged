package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumicadditions.blockentity.HyperNodeCreatorBlockEntity;
import thaumicadditions.registry.ModBlockEntities;

public final class HyperNodeCreatorBlock extends Block implements EntityBlock, thaumcraft.wand.WandInteractable {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public HyperNodeCreatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult onWandUseOn(Level level, BlockPos pos, Player player, net.minecraft.world.item.ItemStack wand,
        net.minecraft.core.Direction side) {
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, side, wand)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof HyperNodeCreatorBlockEntity machine) {
            machine.toggleMode();
            level.playSound(null, pos, thaumcraft.registry.ModSounds.CRYSTAL.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.2F, 1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (thaumcraft.wand.Wands.isWand(player.getMainHandItem())) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof HyperNodeCreatorBlockEntity machine) {
            server.openMenu(machine, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moved) {
        if (!state.is(replacement.getBlock()) && level.getBlockEntity(pos) instanceof HyperNodeCreatorBlockEntity machine) {
            net.minecraft.world.Containers.dropContents(level, pos, machine);
            machine.clearContent();
        }
        super.onRemove(state, level, pos, replacement, moved);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HyperNodeCreatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type != ModBlockEntities.HYPER_NODE_CREATOR.get() ? null :
            (tickLevel, pos, current, entity) -> {
                if (entity instanceof HyperNodeCreatorBlockEntity machine) {
                    if (tickLevel.isClientSide) {
                        machine.clientTick();
                    } else {
                        machine.serverTick();
                    }
                }
            };
    }
}
