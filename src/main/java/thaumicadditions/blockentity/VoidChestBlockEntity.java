package thaumicadditions.blockentity;

import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.crafting.VoidChestLoot;
import thaumicadditions.menu.VoidChestMenu;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModSounds;

public final class VoidChestBlockEntity extends InventoryMachineBlockEntity {
    private static final int[] SLOTS = IntStream.range(0, 72).toArray();
    private boolean generated;
    private boolean pendingLoot;

    public VoidChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOID_CHEST.get(), pos, state, 72);
    }

    public void generateLoot() {
        if (generated) {
            return;
        }
        if (level == null) {
            pendingLoot = true;
            setChanged();
            return;
        }
        if (level.isClientSide) {
            return;
        }
        pendingLoot = false;
        generated = true;
        if (isEmpty()) {
            var loot = VoidChestLoot.roll(level.random, items.size());
            for (int index = 0; index < items.size(); index++) {
                items.set(index, loot.get(index));
            }
        }
        setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (pendingLoot) {
            generateLoot();
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS.clone();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    public void serverTick() {
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new VoidChestMenu(id, inventory, this);
    }

    @Override
    public void startOpen(Player player) {
        if (level != null && !level.isClientSide && !player.isSpectator()) {
            level.playSound(null, worldPosition, ModSounds.VOIDCHESTOPEN.get(), SoundSource.BLOCKS, 1, 1);
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (level != null && !level.isClientSide && !player.isSpectator()) {
            level.playSound(null, worldPosition, ModSounds.VOIDCHESTCLOSE.get(), SoundSource.BLOCKS, 1, 1);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("loot_generated", generated);
        tag.putBoolean("pending_loot", pendingLoot);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        generated = tag.getBoolean("loot_generated");
        pendingLoot = tag.getBoolean("pending_loot");
    }
}
