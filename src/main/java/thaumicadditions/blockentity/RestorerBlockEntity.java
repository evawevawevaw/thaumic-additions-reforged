package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.aspect.Aspect;
import thaumcraft.vis.VisNet;
import thaumicadditions.menu.RestorerMenu;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModSounds;

public final class RestorerBlockEntity extends InventoryMachineBlockEntity {
    private int progress;
    private int earth;
    private int soundTicks;
    private int activeSlot = -1;
    private ItemStack target = ItemStack.EMPTY;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? progress : activeSlot;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public RestorerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESTORER.get(), pos, state, 12);
    }

    public ContainerData progressData() {
        return data;
    }

    private int emptyOutput() {
        for (int slot = 6; slot < 12; slot++) {
            if (items.get(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        int output = emptyOutput();
        int selected = -1;
        for (int slot = 0; slot < 6; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isDamageableItem()) {
                if (stack.getDamageValue() == 0 && output >= 0) {
                    items.set(output, stack);
                    items.set(slot, ItemStack.EMPTY);
                    output = emptyOutput();
                    changed();
                } else if (stack.getDamageValue() > 0 && selected < 0) {
                    selected = slot;
                }
            }
        }
        ItemStack current = selected < 0 ? ItemStack.EMPTY : items.get(selected);
        if (selected != activeSlot || !ItemStack.isSameItemSameComponents(current, target)) {
            progress = 0;
            soundTicks = 0;
            activeSlot = selected;
            target = current.copyWithCount(current.isEmpty() ? 0 : 1);
            changed();
        }
        if (selected >= 0 && output >= 0) {
            if (earth < 2) {
                int drained = VisNet.drainVis(level, worldPosition, Aspect.EARTH, 2 - earth);
                if (drained > 0) {
                    earth += drained;
                    changed();
                }
            }
            if (earth >= 2) {
                earth -= 2;
                progress++;
                soundTicks++;
                if (progress >= 5) {
                    current.setDamageValue(Math.max(0, current.getDamageValue() - 1));
                    progress = 0;
                    target = current.copyWithCount(1);
                    if (current.getDamageValue() == 0) {
                        items.set(output, current);
                        items.set(selected, ItemStack.EMPTY);
                        activeSlot = -1;
                        target = ItemStack.EMPTY;
                    }
                }
                if (soundTicks >= 40) {
                    soundTicks = 0;
                    level.playSound(null, worldPosition, ModSounds.TINKERING.get(), SoundSource.BLOCKS, 1, 1);
                }
                changed();
            }
        }
        syncIfNeeded();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot < 6 && stack.isDamageableItem() && stack.isDamaged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{6, 7, 8, 9, 10, 11} : new int[]{0, 1, 2, 3, 4, 5};
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 6;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new RestorerMenu(id, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", progress);
        tag.putInt("earth", earth);
        tag.putInt("active_slot", activeSlot);
        tag.putInt("sound_ticks", soundTicks);
        tag.put("target", target.saveOptional(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = Math.clamp(tag.getInt("progress"), 0, 4);
        earth = Math.clamp(tag.getInt("earth"), 0, 1);
        activeSlot = tag.contains("active_slot") ? Math.clamp(tag.getInt("active_slot"), -1, 5) : -1;
        soundTicks = Math.clamp(tag.getInt("sound_ticks"), 0, 39);
        target = ItemStack.parseOptional(registries, tag.getCompound("target"));
    }
}
