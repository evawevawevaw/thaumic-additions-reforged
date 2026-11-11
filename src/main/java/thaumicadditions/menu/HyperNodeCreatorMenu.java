package thaumicadditions.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.blockentity.HyperNodeCreatorBlockEntity;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModMenus;

public final class HyperNodeCreatorMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    private final BlockPos pos;

    public HyperNodeCreatorMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, new SimpleContainer(36), new SimpleContainerData(2), pos);
    }

    public HyperNodeCreatorMenu(int id, Inventory inventory, HyperNodeCreatorBlockEntity machine) {
        this(id, inventory, machine, machine.nodeData(), machine.getBlockPos());
    }

    private HyperNodeCreatorMenu(int id, Inventory inventory, Container container, ContainerData data, BlockPos pos) {
        super(ModMenus.HYPER_NODE_CREATOR.get(), id);
        this.container = container;
        this.data = data;
        this.pos = pos;
        checkContainerSize(container, 36);
        checkContainerDataCount(data, 2);
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(container, row * 9 + column, 8 + 18 * column, 16 + 18 * row));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, 8 + column * 18, 102 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 160));
        }
        addDataSlots(data);
    }

    public boolean primalsOnly() {
        return (data.get(0) & 1) != 0;
    }

    public int totalItems() {
        return data.get(1);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player) && player.distanceToSqr(pos.getCenter()) <= 64
            && player.level().getBlockState(pos).is(ModBlocks.HYPER_NODE_CREATOR.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 36 ? !moveItemStackTo(stack, 36, slots.size(), true) : !moveItemStackTo(stack, 0, 36, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return original;
    }
}
