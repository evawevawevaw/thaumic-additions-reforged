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
import thaumicadditions.blockentity.DarkGeneratorBlockEntity;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModMenus;

public final class DarkGeneratorMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    private final BlockPos pos;

    public DarkGeneratorMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, new SimpleContainer(2), new SimpleContainerData(4), pos);
    }

    public DarkGeneratorMenu(int id, Inventory inventory, DarkGeneratorBlockEntity machine) {
        this(id, inventory, machine, machine.progressData(), machine.getBlockPos());
    }

    private DarkGeneratorMenu(int id, Inventory inventory, Container container, ContainerData data, BlockPos pos) {
        super(ModMenus.DARK_GENERATOR.get(), id);
        this.container = container;
        this.data = data;
        this.pos = pos;
        checkContainerSize(container, 2);
        checkContainerDataCount(data, 4);
        addSlot(new Slot(container, 0, 45, 32 + MachineMenuLayout.HEADER) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(net.minecraft.world.item.Items.WHEAT_SEEDS);
            }
        });
        addSlot(new Slot(container, 1, 113, 32 + MachineMenuLayout.HEADER) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, 8 + column * 18,
                    84 + MachineMenuLayout.HEADER + MachineMenuLayout.DARK_INVENTORY_GAP + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18,
                142 + MachineMenuLayout.HEADER + MachineMenuLayout.DARK_INVENTORY_GAP));
        }
        addDataSlots(data);
    }

    public int progressPixels(int width) {
        return Math.clamp(data.get(0) * width / DarkGeneratorBlockEntity.MAX_PROGRESS, 0, width);
    }

    public int moonPhase() {
        return Math.clamp(data.get(2), 0, 7);
    }

    public int brightness() {
        return Math.clamp(data.get(3), 0, 15);
    }

    public boolean hasObelisk() {
        return data.get(1) != 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player) && player.distanceToSqr(pos.getCenter()) <= 64
            && player.level().getBlockState(pos).is(ModBlocks.DARK_GENERATOR.get());
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
