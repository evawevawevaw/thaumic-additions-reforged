package thaumicadditions.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import thaumcraft.item.gear.WarpingGear;

public final class WarpingBlockItem extends BlockItem implements WarpingGear {
    private final int warp;

    public WarpingBlockItem(Block block, Properties properties, int warp) {
        super(block, properties);
        this.warp = warp;
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return warp;
    }
}
