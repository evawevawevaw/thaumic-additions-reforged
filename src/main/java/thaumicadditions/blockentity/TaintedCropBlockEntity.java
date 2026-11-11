package thaumicadditions.blockentity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import thaumcraft.taint.TaintBiomes;
import thaumicadditions.block.TaintedCropBlock;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModBlocks;

public final class TaintedCropBlockEntity extends BlockEntity {
    private int growthTicks;

    public TaintedCropBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TAINTED_CROPS.get(), pos, state);
    }

    public void tickGrowth() {
        if (level == null || level.isClientSide || !getBlockState().is(ModBlocks.TAINTED_CROPS.get())) {
            return;
        }
        BlockState state = getBlockState();
        if (!state.canSurvive(level, worldPosition)) {
            level.destroyBlock(worldPosition, true);
            return;
        }
        int age = state.getValue(TaintedCropBlock.AGE);
        if (age == 4) {
            return;
        }
        growthTicks = Math.min(4000, growthTicks + 1);
        int threshold = TaintBiomes.isTainted(level, worldPosition) ? 3550 : 4000;
        if (growthTicks >= threshold) {
            level.setBlock(worldPosition, state.setValue(TaintedCropBlock.AGE, age + 1), 3);
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("growth_ticks", growthTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        growthTicks = Math.clamp(tag.getInt("growth_ticks"), 0, 4000);
    }
}
