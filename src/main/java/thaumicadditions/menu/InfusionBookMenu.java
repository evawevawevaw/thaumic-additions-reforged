package thaumicadditions.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModMenus;

public final class InfusionBookMenu extends AbstractContainerMenu {
    private final boolean dark;

    public InfusionBookMenu(int id, Inventory inventory, boolean dark) {
        super(ModMenus.INFUSION_BOOK.get(), id);
        this.dark = dark;
    }

    public boolean dark() {
        return dark;
    }

    @Override
    public boolean stillValid(Player player) {
        var item = dark ? ModItems.DARK_INFUSER_BOOK.get() : ModItems.INFUSER_BOOK.get();
        return player.getMainHandItem().is(item) || player.getOffhandItem().is(item);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
