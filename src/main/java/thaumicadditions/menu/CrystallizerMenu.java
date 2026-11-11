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
import thaumicadditions.blockentity.CrystallizerBlockEntity;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModMenus;

public final class CrystallizerMenu extends AbstractContainerMenu {
    private static final int[][] LOCATIONS = {{80, 70}, {131, 41}, {131, 99}, {30, 41}, {30, 99}, {80, 13}, {80, 128}, {8, 128}};
    private final Container container;
    private final ContainerData data;
    private final BlockPos pos;

    public CrystallizerMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, new SimpleContainer(8), new SimpleContainerData(2), pos);
    }

    public CrystallizerMenu(int id, Inventory inventory, CrystallizerBlockEntity machine) {
        this(id, inventory, machine, machine.progressData(), machine.getBlockPos());
    }

    private CrystallizerMenu(int id, Inventory inventory, Container container, ContainerData data, BlockPos pos) {
        super(ModMenus.CRYSTALLIZER.get(), id);
        this.container = container;
        this.data = data;
        this.pos = pos;
        checkContainerSize(container, 8);
        checkContainerDataCount(data, 2);
        for (int i = 0; i < LOCATIONS.length; i++) {
            int index = i;
            addSlot(new Slot(container, i, LOCATIONS[i][0], MachineMenuLayout.crystallizerY(LOCATIONS[i][1])) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return index == 0 && stack.is(ModItems.DEPLETED_SHARD.get()) || index == 7;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return index != 7;
                }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, 8 + column * 18, MachineMenuLayout.crystallizerY(158 + row * 18)));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, MachineMenuLayout.crystallizerY(216)));
        }
        addDataSlots(data);
    }

    public int progressPixels(int height) {
        return Math.clamp(data.get(0) * height / CrystallizerBlockEntity.WORK_TICKS, 0, height);
    }

    public int chargePixels(int height) {
        return Math.clamp(data.get(1) * height / 256, 0, height);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player) && player.distanceToSqr(pos.getCenter()) <= 64
            && player.level().getBlockState(pos).is(ModBlocks.THAUMIC_CRYSTALLIZER.get());
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
        if (index < 8) {
            if (!moveItemStackTo(stack, 8, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!stack.is(ModItems.DEPLETED_SHARD.get()) || !moveItemStackTo(stack, 0, 1, false)) {
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
