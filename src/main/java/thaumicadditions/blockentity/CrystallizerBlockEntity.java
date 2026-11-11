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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicPort;
import thaumicadditions.menu.CrystallizerMenu;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModSounds;

public final class CrystallizerBlockEntity extends InventoryMachineBlockEntity implements MagicPort {
    public static final int WORK_TICKS = 5000;
    private int work;
    private int charge;
    private int pendingShard = -1;
    private int displayMask;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) { case 0 -> work; case 1 -> charge; default -> 0; };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public CrystallizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTALLIZER.get(), pos, state, 8);
    }

    public ContainerData progressData() {
        return data;
    }

    public boolean hasCrystal(int slot) {
        return (displayMask & 1 << slot) != 0;
    }

    public static Item shardItem(int kind) {
        return switch (kind) {
            case 0 -> thaumcraft.registry.ModItems.AIR_SHARD.get();
            case 1 -> thaumcraft.registry.ModItems.FIRE_SHARD.get();
            case 2 -> thaumcraft.registry.ModItems.WATER_SHARD.get();
            case 3 -> thaumcraft.registry.ModItems.EARTH_SHARD.get();
            case 4 -> thaumcraft.registry.ModItems.ORDER_SHARD.get();
            case 5 -> thaumcraft.registry.ModItems.ENTROPY_SHARD.get();
            default -> throw new IllegalArgumentException("Invalid primal shard: " + kind);
        };
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (charge == 0 && work < WORK_TICKS) {
            for (Direction side : Direction.values()) {
                BlockPos neighbour = worldPosition.relative(side);
                if (canConnect(side) && level.isLoaded(neighbour) && level.getBlockEntity(neighbour) instanceof MagicPort port
                    && port.canConnect(side.getOpposite()) && port.extract(MagicKind.VIS, 1, side.getOpposite(), false) > 0) {
                    charge = 256;
                    changed();
                    break;
                }
            }
        }
        if (!items.get(0).is(ModItems.DEPLETED_SHARD.get())) {
            if (work > 0 || pendingShard >= 0) {
                work = 0;
                pendingShard = -1;
                changed();
            }
        } else {
            if (work < WORK_TICKS && charge > 0) {
                charge--;
                work++;
                changed();
                if (work % 20 == 0) {
                    level.playSound(null, worldPosition, ModSounds.INFUSER.get(), SoundSource.BLOCKS, 0.25F, 1);
                }
            }
            if (work >= WORK_TICKS) {
                if (pendingShard < 0) {
                    pendingShard = level.random.nextInt(6);
                    changed();
                }
                ItemStack result = new ItemStack(shardItem(pendingShard));
                ItemStack output = items.get(pendingShard + 1);
                if (output.isEmpty() || ItemStack.isSameItemSameComponents(output, result)
                    && output.getCount() < Math.min(getMaxStackSize(), output.getMaxStackSize())) {
                    if (output.isEmpty()) {
                        items.set(pendingShard + 1, result);
                    } else {
                        output.grow(1);
                    }
                    items.get(0).shrink(1);
                    work = 0;
                    pendingShard = -1;
                    changed();
                }
            }
        }
        int mask = 0;
        for (int slot = 0; slot < 7; slot++) {
            if (slot == 0 ? items.get(0).is(ModItems.DEPLETED_SHARD.get()) : items.get(slot).is(shardItem(slot - 1))) {
                mask |= 1 << slot;
            }
        }
        if (mask != displayMask) {
            displayMask = mask;
            changed();
        }
        syncIfNeeded();
    }

    @Override
    public boolean canConnect(Direction side) {
        return side != Direction.UP;
    }

    @Override
    public int stored(MagicKind kind) {
        return 0;
    }

    @Override
    public int insert(MagicKind kind, int amount, Direction side, boolean simulate) {
        int accepted = kind == MagicKind.VIS && canConnect(side) && amount > 0 && charge == 0 && work < WORK_TICKS ? 1 : 0;
        if (!simulate && accepted > 0) {
            charge = 256;
            changed();
        }
        return accepted;
    }

    @Override
    public int extract(MagicKind kind, int amount, Direction side, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && stack.is(ModItems.DEPLETED_SHARD.get()) || slot == 7;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{1, 2, 3, 4, 5, 6} : new int[]{0};
    }

    public ItemStack removeDisplayItem() {
        return removeItem(7, getMaxStackSize());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CrystallizerMenu(id, inventory, this);
    }

    private void saveProgress(CompoundTag tag) {
        tag.putInt("work", work);
        tag.putInt("charge", charge);
        tag.putInt("pending_shard", pendingShard);
        tag.putInt("display_mask", displayMask);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveProgress(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        work = Math.clamp(tag.getInt("work"), 0, WORK_TICKS);
        charge = Math.clamp(tag.getInt("charge"), 0, 256);
        pendingShard = tag.contains("pending_shard") ? Math.clamp(tag.getInt("pending_shard"), -1, 5) : -1;
        displayMask = tag.getInt("display_mask") & 127;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveProgress(tag);
        return tag;
    }
}
