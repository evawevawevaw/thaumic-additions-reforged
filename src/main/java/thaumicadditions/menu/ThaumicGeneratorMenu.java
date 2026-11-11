package thaumicadditions.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.blockentity.ThaumicGeneratorBlockEntity;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModMenus;

public final class ThaumicGeneratorMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private final ContainerData data;

    public ThaumicGeneratorMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, pos, new SimpleContainerData(2));
    }

    public ThaumicGeneratorMenu(int id, Inventory inventory, ThaumicGeneratorBlockEntity machine) {
        this(id, inventory, machine.getBlockPos(), machine.energyData());
    }

    private ThaumicGeneratorMenu(int id, Inventory inventory, BlockPos pos, ContainerData data) {
        super(ModMenus.THAUMIC_GENERATOR.get(), id);
        this.pos = pos;
        this.data = data;
        checkContainerDataCount(data, 2);
        addDataSlots(data);
    }

    public int energy() {
        return (data.get(1) & 65535) << 16 | data.get(0) & 65535;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(pos.getCenter()) <= 64 && player.level().getBlockState(pos).is(ModBlocks.THAUMIC_GENERATOR.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
