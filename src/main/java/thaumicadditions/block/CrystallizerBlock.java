package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.wand.WandInteractable;
import thaumicadditions.blockentity.CrystallizerBlockEntity;
import thaumicadditions.registry.ModBlockEntities;

public final class CrystallizerBlock extends Block implements EntityBlock, WandInteractable {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 10, 16);

    public CrystallizerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof CrystallizerBlockEntity machine) {
            server.openMenu(machine, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResult onWandUseOn(Level level, BlockPos pos, Player player, ItemStack wand, Direction side) {
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, side, wand)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CrystallizerBlockEntity machine) {
            ItemStack display = machine.removeDisplayItem();
            if (!display.isEmpty()) {
                Block.popResource(level, pos.above(), display);
                level.playSound(null, pos, thaumcraft.registry.ModSounds.ZAP.get(), SoundSource.BLOCKS, 1, 1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moved) {
        if (!state.is(replacement.getBlock()) && level.getBlockEntity(pos) instanceof CrystallizerBlockEntity machine) {
            Containers.dropContents(level, pos, machine);
            machine.clearContent();
        }
        super.onRemove(state, level, pos, replacement, moved);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystallizerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != ModBlockEntities.CRYSTALLIZER.get() ? null :
            (tickLevel, pos, current, entity) -> {
                if (entity instanceof CrystallizerBlockEntity machine) {
                    machine.serverTick();
                }
            };
    }
}
