package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.machine.VisEnergyBuffer;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicPort;
import thaumicadditions.menu.ThaumicGeneratorMenu;
import thaumicadditions.registry.ModBlockEntities;

public final class ThaumicGeneratorBlockEntity extends InventoryMachineBlockEntity implements MagicPort {
    private static final Direction[] OUTPUT_ORDER = {Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH};
    private final VisEnergyBuffer energy = new VisEnergyBuffer();
    private int outputMask;
    private final IEnergyStorage storage = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int requested, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int requested, boolean simulate) {
            if (level == null || level.isClientSide) {
                return 0;
            }
            int extracted = energy.extract(requested, simulate);
            if (!simulate && extracted > 0) {
                changed();
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return energy.energyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return VisEnergyBuffer.CAPACITY;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? energy.energyStored() & 65535 : energy.energyStored() >>> 16;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ThaumicGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THAUMIC_GENERATOR.get(), pos, state, 0);
    }

    public IEnergyStorage energyStorage() {
        return storage;
    }

    public ContainerData energyData() {
        return data;
    }

    public boolean outputConnected(Direction side) {
        return (outputMask & 1 << side.ordinal()) != 0;
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (energy.canConvertVis() && level.random.nextBoolean()) {
            for (Direction side : Direction.values()) {
                BlockPos neighbour = worldPosition.relative(side);
                if (level.isLoaded(neighbour) && level.getBlockEntity(neighbour) instanceof MagicPort port
                    && port.canConnect(side.getOpposite()) && port.extract(MagicKind.VIS, 1, side.getOpposite(), false) > 0) {
                    energy.convertVis();
                    changed();
                    break;
                }
            }
        }
        int mask = 0;
        for (Direction side : OUTPUT_ORDER) {
            BlockPos neighbour = worldPosition.relative(side);
            if (!level.isLoaded(neighbour)) {
                continue;
            }
            IEnergyStorage receiver = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighbour, side.getOpposite());
            if (receiver != null && receiver != storage && receiver.canReceive()) {
                mask |= 1 << side.ordinal();
                int offer = energy.extract(VisEnergyBuffer.TRANSFER_LIMIT, true);
                int accepted = Math.clamp(receiver.receiveEnergy(offer, false), 0, offer);
                if (accepted > 0) {
                    energy.extract(accepted, false);
                    changed();
                }
            }
            var mana = thaumicadditions.compat.BotaniaBridge.receiver(level, neighbour, side.getOpposite());
            if (mana != null) {
                mask |= 1 << side.ordinal();
                if (energy.emitMana(mana) > 0) {
                    changed();
                }
            }
        }
        if (mask != outputMask) {
            outputMask = mask;
            changed();
        }
        syncIfNeeded();
    }

    @Override
    public boolean canConnect(Direction side) {
        return true;
    }

    @Override
    public int stored(MagicKind kind) {
        return 0;
    }

    @Override
    public int insert(MagicKind kind, int amount, Direction side, boolean simulate) {
        return 0;
    }

    @Override
    public int extract(MagicKind kind, int amount, Direction side, boolean simulate) {
        return 0;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ThaumicGeneratorMenu(id, inventory, this);
    }

    private void saveProgress(CompoundTag tag) {
        tag.putInt("energy_units", energy.savedUnits());
        tag.putInt("outputs", outputMask);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveProgress(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy.loadUnits(tag.getInt("energy_units"));
        outputMask = tag.getInt("outputs") & 63;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveProgress(tag);
        return tag;
    }
}
