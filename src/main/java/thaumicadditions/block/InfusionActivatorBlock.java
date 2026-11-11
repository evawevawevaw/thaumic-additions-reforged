package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.wand.WandInteractable;
import thaumicadditions.blockentity.InfusionActivatorBlockEntity;
import thaumicadditions.registry.ModBlockEntities;

public final class InfusionActivatorBlock extends Block implements EntityBlock, WandInteractable {
    public InfusionActivatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player
            && level.getBlockEntity(pos) instanceof InfusionActivatorBlockEntity machine) {
            machine.setOwner(player);
        }
    }

    @Override
    public InteractionResult onWandUseOn(Level level, BlockPos pos, Player player, ItemStack wand, Direction side) {
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, side, wand)
            || !(level.getBlockEntity(pos) instanceof InfusionActivatorBlockEntity machine) || machine.hasWand()) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide && machine.install(wand, player)) {
            wand.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty() || !level.mayInteract(player, pos)
            || !player.mayUseItemAt(pos, hit.getDirection(), ItemStack.EMPTY)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof InfusionActivatorBlockEntity machine) {
            ItemStack stack = machine.removeWand();
            if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moved) {
        if (!state.is(replacement.getBlock()) && level.getBlockEntity(pos) instanceof InfusionActivatorBlockEntity machine) {
            Block.popResource(level, pos, machine.removeWand());
        }
        super.onRemove(state, level, pos, replacement, moved);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionActivatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type != ModBlockEntities.INFUSION_ACTIVATOR.get() ? null : (tickLevel, pos, current, entity) -> {
            if (entity instanceof InfusionActivatorBlockEntity machine) {
                machine.tick();
            }
        };
    }
}
