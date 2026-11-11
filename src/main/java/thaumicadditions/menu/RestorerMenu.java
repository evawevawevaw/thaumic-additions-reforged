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
import thaumicadditions.blockentity.RestorerBlockEntity;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModMenus;

public final class RestorerMenu extends AbstractContainerMenu {
    private static final int[][] LOCATIONS = {{37, 16}, {37, 34}, {37, 52}, {55, 16}, {55, 34}, {55, 52}, {90, 16}, {90, 34}, {90, 52}, {108, 16}, {108, 34}, {108, 52}};
    private final Container container;
    private final ContainerData data;
    private final BlockPos pos;

    public RestorerMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, new SimpleContainer(12), new SimpleContainerData(2), pos);
    }

    public RestorerMenu(int id, Inventory inventory, RestorerBlockEntity machine) {
        this(id, inventory, machine, machine.progressData(), machine.getBlockPos());
    }

    private RestorerMenu(int id, Inventory inventory, Container container, ContainerData data, BlockPos pos) {
        super(ModMenus.RESTORER.get(), id);
        this.container = container;
        this.data = data;
        this.pos = pos;
        checkContainerSize(container, 12);
        checkContainerDataCount(data, 2);
        for (int i = 0; i < LOCATIONS.length; i++) {
            int index = i;
            addSlot(new Slot(container, i, LOCATIONS[i][0], LOCATIONS[i][1] + MachineMenuLayout.HEADER) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return index < 6 && stack.isDamageableItem() && stack.isDamaged();
                }

                @Override
                public boolean mayPickup(Player player) {
                    return true;
                }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, 8 + column * 18, 84 + MachineMenuLayout.HEADER + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142 + MachineMenuLayout.HEADER));
        }
        addDataSlots(data);
    }

    public int progressPixels(int height) {
        return Math.clamp(data.get(0) * height / 5, 0, height);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player) && player.distanceToSqr(pos.getCenter()) <= 64
            && player.level().getBlockState(pos).is(ModBlocks.THAUMIC_RESTORER.get());
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
        if (index < 12) {
            if (!moveItemStackTo(stack, 12, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!stack.isDamageableItem() || !stack.isDamaged() || !moveItemStackTo(stack, 0, 6, false)) {
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
