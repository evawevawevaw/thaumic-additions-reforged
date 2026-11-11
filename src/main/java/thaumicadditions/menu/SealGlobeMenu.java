package thaumicadditions.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.blockentity.SealBlockEntity;
import thaumicadditions.registry.ModMenus;

public final class SealGlobeMenu extends AbstractContainerMenu {
    private final BlockPos pos;

    public SealGlobeMenu(int id, Inventory inventory, BlockPos pos) {
        super(ModMenus.SEAL_GLOBE.get(), id);
        this.pos = pos;
    }

    public BlockPos pos() {
        return pos;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().getBlockEntity(pos) instanceof SealBlockEntity && player.distanceToSqr(pos.getCenter()) <= 64;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
