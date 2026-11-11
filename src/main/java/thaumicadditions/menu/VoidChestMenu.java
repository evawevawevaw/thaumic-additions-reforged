package thaumicadditions.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.blockentity.VoidChestBlockEntity;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModMenus;

public final class VoidChestMenu extends AbstractContainerMenu {
    private final Container container;
    private final BlockPos pos;

    public VoidChestMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, new SimpleContainer(72), pos);
    }

    public VoidChestMenu(int id, Inventory inventory, VoidChestBlockEntity chest) {
        this(id, inventory, chest, chest.getBlockPos());
    }

    private VoidChestMenu(int id, Inventory inventory, Container container, BlockPos pos) {
        super(ModMenus.VOID_CHEST.get(), id);
        this.container = container;
        this.pos = pos;
        checkContainerSize(container, 72);
        container.startOpen(inventory.player);
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(container, row * 9 + column, 8 + column * 18, 9 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, 8 + column * 18, 158 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 216));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player) && player.distanceToSqr(pos.getCenter()) <= 64
            && player.level().getBlockState(pos).is(ModBlocks.VOID_CHEST.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= 72 || !slots.get(index).hasItem()) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (!moveItemStackTo(stack, 72, slots.size(), true)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return copy;
    }
}
