package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.block.VisPipeBlock;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.PipeNetwork;
import thaumicadditions.registry.ModBlockEntities;

public final class VisPipeBlockEntity extends MagicBufferBlockEntity {
    private int disabledSides;
    private int displayVis;
    private int displayTaint;

    public VisPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VIS_PIPE.get(), pos, state);
    }

    public boolean filter() {
        return getBlockState().getBlock() instanceof VisPipeBlock block && block.filter();
    }

    @Override
    public int capacity() {
        return PipeNetwork.CAPACITY;
    }

    @Override
    public boolean canConnect(Direction side) {
        return (disabledSides & 1 << side.ordinal()) == 0
            && (!filter() || getBlockState().getValue(VisPipeBlock.AXIS) == side.getAxis());
    }

    public void toggleSide(Direction side) {
        if (level == null || level.isClientSide) {
            return;
        }
        disabledSides ^= 1 << side.ordinal();
        PipeNetwork.invalidate(level);
        changed();
        level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
    }

    @Override
    public int stored(MagicKind kind) {
        if (level == null || level.isClientSide) {
            return kind == MagicKind.VIS ? displayVis : displayTaint;
        }
        return PipeNetwork.get(this).stored(kind);
    }

    @Override
    public int totalStored() {
        return stored(MagicKind.VIS) + stored(MagicKind.TAINT);
    }

    public int localStored(MagicKind kind) {
        return kind == MagicKind.VIS ? vis : taint;
    }

    public void setLocal(MagicKind kind, int amount) {
        setLocalCounts(kind == MagicKind.VIS ? amount : vis, kind == MagicKind.TAINT ? amount : taint);
    }

    public void setLocalCounts(int newVis, int newTaint) {
        if (newVis < 0 || newTaint < 0 || newVis + newTaint > capacity()) {
            throw new IllegalArgumentException("Invalid local pipe contents");
        }
        if (newVis != vis || newTaint != taint) {
            vis = newVis;
            taint = newTaint;
            changed();
        }
    }

    public void updateDisplay(int newVis, int newTaint) {
        if (displayVis != newVis || displayTaint != newTaint) {
            displayVis = newVis;
            displayTaint = newTaint;
            changed();
        }
    }

    @Override
    public int insert(MagicKind kind, int amount, Direction side, boolean simulate) {
        return level == null || level.isClientSide || !canConnect(side) ? 0 : PipeNetwork.get(this).insert(this, kind, amount, simulate);
    }

    @Override
    public int extract(MagicKind kind, int amount, Direction side, boolean simulate) {
        return level == null || level.isClientSide || !canConnect(side) ? 0 : PipeNetwork.get(this).extract(kind, amount, simulate);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            PipeNetwork.invalidate(level);
            level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide) {
            PipeNetwork.invalidate(level);
        }
        super.setRemoved();
    }

    @Override
    public void serverTick() {
        if (level != null && !level.isClientSide) {
            PipeNetwork.get(this).tick(level);
            syncIfNeeded();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("disabled_sides", disabledSides);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        disabledSides = tag.getInt("disabled_sides") & 63;
        displayVis = Math.max(0, tag.getInt("network_vis"));
        displayTaint = Math.max(0, tag.getInt("network_taint"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("network_vis", displayVis);
        tag.putInt("network_taint", displayTaint);
        return tag;
    }
}
