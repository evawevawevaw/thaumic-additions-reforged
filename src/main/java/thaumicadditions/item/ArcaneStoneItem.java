package thaumicadditions.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.registry.ModBlocks;

public final class ArcaneStoneItem extends Item {
    public ArcaneStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !context.getLevel().mayInteract(player, context.getClickedPos())
            || !player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), context.getItemInHand())) {
            return InteractionResult.PASS;
        }
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        Block result = null;
        if (player.isShiftKeyDown()) {
            if (state.is(Blocks.DIAMOND_BLOCK)) {
                result = ModBlocks.RUNIC_MATRIX.get();
            } else if (state.is(ModBlocks.ARCANE_STONE.get())) {
                result = Blocks.STONE;
            } else if (state.is(ModBlocks.ARCANE_STONE_BRICKS.get())) {
                result = Blocks.STONE_BRICKS;
            }
        } else if (state.is(Blocks.STONE)) {
            result = ModBlocks.ARCANE_STONE.get();
        } else if (state.is(Blocks.STONE_BRICKS)) {
            result = ModBlocks.ARCANE_STONE_BRICKS.get();
        }
        if (result == null) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide && !context.getLevel().setBlock(context.getClickedPos(), result.defaultBlockState(), Block.UPDATE_ALL)) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
