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
import net.neoforged.neoforge.common.NeoForge;
import thaumicadditions.blockentity.InfuserBlockEntity;
import thaumicadditions.event.InfuserOutputEvent;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModMenus;

public final class InfuserMenu extends AbstractContainerMenu {
    private static final int[][] REGULAR_SLOTS = {{80, 72}, {80, 11}, {50, 55}, {110, 55}, {28, 103}, {80, 106}, {132, 103}, {80, 135}};
    private static final int[][] DARK_SLOTS = {{80, 72}, {80, 16}, {26, 54}, {133, 54}, {49, 117}, {112, 119}};
    private final Container container;
    private final ContainerData data;
    private final BlockPos pos;
    private final boolean dark;
    private final int machineSlots;

    public InfuserMenu(int id, Inventory inventory, BlockPos pos, boolean dark) {
        this(id, inventory, new SimpleContainer(dark ? 6 : 8), new SimpleContainerData(4), pos, dark);
    }

    public InfuserMenu(int id, Inventory inventory, InfuserBlockEntity machine) {
        this(id, inventory, machine, machine.progressData(), machine.getBlockPos(), machine.dark());
    }

    private InfuserMenu(int id, Inventory inventory, Container container, ContainerData data, BlockPos pos, boolean dark) {
        super(ModMenus.INFUSER.get(), id);
        this.container = container;
        this.data = data;
        this.pos = pos;
        this.dark = dark;
        machineSlots = dark ? 6 : 8;
        checkContainerSize(container, machineSlots);
        checkContainerDataCount(data, 4);
        int[][] locations = dark ? DARK_SLOTS : REGULAR_SLOTS;
        for (int i = 0; i < locations.length; i++) {
            int index = i;
            boolean output = i == 0 || !dark && i == 7;
            addSlot(new Slot(container, i, locations[i][0], locations[i][1]) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return !output && container.canPlaceItem(index, stack);
                }

                @Override
                public boolean mayPickup(Player player) {
                    if (output && index == 0 && !player.level().isClientSide && container instanceof InfuserBlockEntity machine) {
                        return !NeoForge.EVENT_BUS.post(new InfuserOutputEvent(machine, player, getItem())).isCanceled();
                    }
                    return true;
                }
            });
        }
        int x = dark ? 7 : 8;
        int y = dark ? 157 : 158;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, x + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, x + column * 18, dark ? 215 : 216));
        }
        addDataSlots(data);
    }

    public boolean dark() {
        return dark;
    }

    public int progressPixels(boolean taint, int height) {
        int amount = data.get(taint ? 1 : 0);
        int cost = data.get(taint ? 3 : 2);
        return cost <= 0 ? 0 : Math.clamp(amount * height / cost, 0, height);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player) && player.distanceToSqr(pos.getCenter()) <= 64
            && player.level().getBlockState(pos).is(dark ? ModBlocks.DARK_INFUSER.get() : ModBlocks.INFUSER.get());
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
        if (index < machineSlots) {
            if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 1, dark ? 6 : 7, false)) {
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
