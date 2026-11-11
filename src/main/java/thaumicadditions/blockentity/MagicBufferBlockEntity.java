package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectContainer;
import thaumcraft.aspect.AspectList;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicPort;

public abstract class MagicBufferBlockEntity extends BlockEntity implements MagicPort, AspectContainer {
    protected int vis;
    protected int taint;
    private boolean needsSync;

    protected MagicBufferBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract int capacity();

    protected int storageLimit() {
        return capacity();
    }

    protected int insertionLimit() {
        return capacity();
    }

    @Override
    public boolean canConnect(Direction side) {
        return true;
    }

    @Override
    public int stored(MagicKind kind) {
        return kind == MagicKind.VIS ? vis : taint;
    }

    public int totalStored() {
        return vis + taint;
    }

    @Override
    public int insert(MagicKind kind, int amount, Direction side, boolean simulate) {
        if (!canConnect(side) || amount <= 0) {
            return 0;
        }
        int accepted = Math.min(amount, Math.max(0, insertionLimit() - totalStored()));
        if (!simulate && accepted > 0) {
            if (kind == MagicKind.VIS) {
                vis += accepted;
            } else {
                taint += accepted;
            }
            changed();
        }
        return accepted;
    }

    @Override
    public int extract(MagicKind kind, int amount, Direction side, boolean simulate) {
        if (!canConnect(side) || amount <= 0) {
            return 0;
        }
        int extracted = Math.min(amount, stored(kind));
        if (!simulate && extracted > 0) {
            if (kind == MagicKind.VIS) {
                vis -= extracted;
            } else {
                taint -= extracted;
            }
            changed();
        }
        return extracted;
    }

    protected final void changed() {
        setChanged();
        needsSync = true;
    }

    protected final void syncIfNeeded() {
        if (needsSync && level != null && !level.isClientSide && level.getGameTime() % 5 == 0) {
            needsSync = false;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public abstract void serverTick();

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("vis", vis);
        tag.putInt("taint", taint);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        vis = Math.clamp(tag.getInt("vis"), 0, storageLimit());
        taint = Math.clamp(tag.getInt("taint"), 0, storageLimit() - vis);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public AspectList getAspects() {
        return new AspectList().add(Aspect.MAGIC, stored(MagicKind.VIS)).add(Aspect.TAINT, stored(MagicKind.TAINT));
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return false;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return amount >= 0 && containerContains(aspect) >= amount;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return aspect == Aspect.MAGIC ? stored(MagicKind.VIS) : aspect == Aspect.TAINT ? stored(MagicKind.TAINT) : 0;
    }
}
