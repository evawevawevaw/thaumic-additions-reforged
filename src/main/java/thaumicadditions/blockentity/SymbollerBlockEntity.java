package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.essentia.JarBlockEntity;
import thaumicadditions.block.SymbollerBlock;
import thaumicadditions.item.SealSymbolItem;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.registry.ModItems;

public final class SymbollerBlockEntity extends InventoryMachineBlockEntity {
    public SymbollerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SYMBOLLER.get(), pos, state, 1);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(ModItems.SEAL_SYMBOL.get()) && SealSymbolItem.aspect(stack) == null;
    }

    @Override
    public void serverTick() {
        boolean hasJar = level.getBlockEntity(worldPosition.above()) instanceof JarBlockEntity;
        BlockState state = getBlockState();
        if (state.getValue(SymbollerBlock.HAS_JAR) != hasJar) {
            level.setBlock(worldPosition, state.setValue(SymbollerBlock.HAS_JAR, hasJar), 3);
        }
        ItemStack symbol = items.getFirst();
        if (!symbol.isEmpty() && canPlaceItem(0, symbol) && level.getBlockEntity(worldPosition.above()) instanceof JarBlockEntity jar) {
            var aspect = jar.getAspect();
            if (aspect != null && jar.takeFromContainer(aspect, 1)) {
                symbol.set(ModDataComponents.SYMBOL_ASPECT.get(), aspect.getTag());
                changed();
            }
        }
        syncIfNeeded();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return null;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        net.minecraft.world.ContainerHelper.saveAllItems(tag, items, registries);
        return tag;
    }
}
