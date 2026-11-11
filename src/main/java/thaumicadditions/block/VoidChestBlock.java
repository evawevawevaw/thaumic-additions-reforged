package thaumicadditions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumicadditions.blockentity.VoidChestBlockEntity;
import thaumicadditions.registry.ModDataComponents;

public final class VoidChestBlock extends Block implements EntityBlock {
    public VoidChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidChestBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && Boolean.TRUE.equals(stack.get(ModDataComponents.VOID_CHEST_LOOT.get()))
            && level.getBlockEntity(pos) instanceof VoidChestBlockEntity chest) {
            chest.generateLoot();
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof VoidChestBlockEntity chest) {
            server.openMenu(chest, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moved) {
        if (state.getBlock() != replacement.getBlock() && level.getBlockEntity(pos) instanceof VoidChestBlockEntity chest) {
            Containers.dropContents(level, pos, chest);
            chest.clearContent();
        }
        super.onRemove(state, level, pos, replacement, moved);
    }
}
