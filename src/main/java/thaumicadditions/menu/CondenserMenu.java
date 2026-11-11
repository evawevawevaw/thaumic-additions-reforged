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
import thaumicadditions.blockentity.CondenserBlockEntity;
import thaumicadditions.machine.CondenserCycle;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModMenus;

public final class CondenserMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    private final BlockPos pos;

    public CondenserMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, new SimpleContainer(2), new SimpleContainerData(7), pos);
    }

    public CondenserMenu(int id, Inventory inventory, CondenserBlockEntity machine) {
        this(id, inventory, machine, machine.progressData(), machine.getBlockPos());
    }

    private CondenserMenu(int id, Inventory inventory, Container container, ContainerData data, BlockPos pos) {
        super(ModMenus.CONDENSER.get(), id);
        this.container = container;
        this.data = data;
        this.pos = pos;
        checkContainerSize(container, 2);
        checkContainerDataCount(data, 7);
        addSlot(new Slot(container, 0, 46, 32) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CondenserBlockEntity.shardColor(stack) >= 0;
            }
        });
        addSlot(new Slot(container, 1, 114, 32) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
        addDataSlots(data);
    }

    public int progressPixels(int height) {
        return data.get(0) < 0 ? 0 : Math.clamp(data.get(0) * height / CondenserCycle.LIFETIME, 0, height);
    }

    public int crystalColor() {
        return 0xFF000000 | (data.get(2) & 65535) << 16 | data.get(1) & 65535;
    }

    public int bufferedVis() {
        return (data.get(4) & 65535) << 16 | data.get(3) & 65535;
    }

    public int upgradeIndex(int slot) {
        return data.get(5 + slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player) && player.distanceToSqr(pos.getCenter()) <= 64
            && player.level().getBlockState(pos).is(ModBlocks.OLD_CONDENSATOR.get());
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
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!slots.getFirst().mayPlace(stack) || !moveItemStackTo(stack, 0, 1, false)) {
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
