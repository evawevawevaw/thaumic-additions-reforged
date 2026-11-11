package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.registry.EldritchContent;
import thaumicadditions.menu.DarkGeneratorMenu;
import thaumicadditions.registry.ModBlockEntities;

public final class DarkGeneratorBlockEntity extends InventoryMachineBlockEntity {
    public static final int MAX_PROGRESS = 1024;
    private int progress;
    private int tick;
    private int scanCountdown;
    private boolean obelisk;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> obelisk ? 1 : 0;
                case 2 -> level == null ? 0 : level.getMoonPhase();
                case 3 -> level == null ? 0 : Math.max(level.getBrightness(LightLayer.SKY, worldPosition), level.getBrightness(LightLayer.BLOCK, worldPosition));
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public DarkGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DARK_GENERATOR.get(), pos, state, 2);
    }

    public ContainerData progressData() {
        return data;
    }

    private boolean findObelisk() {
        BlockPos min = worldPosition.offset(-8, -8, -8);
        BlockPos max = worldPosition.offset(7, 7, 7);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.is(EldritchContent.ELDRITCH_ALTAR.get()) || state.is(EldritchContent.ELDRITCH_OBELISK.get())
                || state.is(EldritchContent.ELDRITCH_CAPSTONE.get())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (--scanCountdown <= 0) {
            scanCountdown = 20;
            boolean found = findObelisk();
            if (found != obelisk) {
                obelisk = found;
                changed();
            }
        }
        ItemStack input = items.get(0);
        if (!input.is(Items.WHEAT_SEEDS)) {
            if (progress != 0 || tick != 0) {
                progress = 0;
                tick = 0;
                changed();
            }
        } else if (obelisk) {
            ItemStack result = new ItemStack(thaumcraft.registry.ModItems.VOID_SEED.get());
            ItemStack output = items.get(1);
            boolean room = output.isEmpty() || ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() < Math.min(getMaxStackSize(), output.getMaxStackSize());
            if (room) {
                tick++;
                if (tick >= 20) {
                    tick = 0;
                    progress = Math.min(MAX_PROGRESS, progress + 15);
                }
                if (progress >= MAX_PROGRESS) {
                    input.shrink(1);
                    if (output.isEmpty()) {
                        items.set(1, result);
                    } else {
                        output.grow(1);
                    }
                    progress = 0;
                }
                changed();
            }
        }
        syncIfNeeded();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && stack.is(Items.WHEAT_SEEDS);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{1} : side == Direction.UP ? new int[]{0} : new int[]{0, 1};
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new DarkGeneratorMenu(id, inventory, this);
    }

    private void saveProgress(CompoundTag tag) {
        tag.putInt("progress", progress);
        tag.putInt("tick", tick);
        tag.putBoolean("obelisk", obelisk);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveProgress(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = Math.clamp(tag.getInt("progress"), 0, MAX_PROGRESS);
        tick = Math.clamp(tag.getInt("tick"), 0, 19);
        obelisk = tag.getBoolean("obelisk");
        scanCountdown = 0;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveProgress(tag);
        return tag;
    }
}
