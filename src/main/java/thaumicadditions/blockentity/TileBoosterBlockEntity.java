package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.machine.TickAcceleration;
import thaumicadditions.registry.ModBlockEntities;

public final class TileBoosterBlockEntity extends BlockEntity {
    private int speed = 1;

    public TileBoosterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TILE_BOOSTER.get(), pos, state);
    }

    public int speed() {
        return Math.min(speed, thaumicadditions.config.ModConfig.MAX_BOOST.get());
    }

    public void serverTick() {
        if (level == null || level.isClientSide || speed == 0) {
            return;
        }
        TickAcceleration.run(() -> {
            for (Direction side : Direction.values()) {
                BlockPos targetPos = worldPosition.relative(side);
                if (!level.isLoaded(targetPos)) {
                    continue;
                }
                BlockEntity target = level.getBlockEntity(targetPos);
                if (target != null && target != this && !(target instanceof TileBoosterBlockEntity)) {
                    tickTarget(target, targetPos);
                }
            }
        });
    }

    private <T extends BlockEntity> void tickTarget(T target, BlockPos pos) {
        for (int i = 0; i < speed(); i++) {
            if (target.isRemoved() || !level.isLoaded(pos) || level.getBlockEntity(pos) != target) {
                return;
            }
            BlockState state = level.getBlockState(pos);
            if (!target.getType().isValid(state)) {
                return;
            }
            @SuppressWarnings("unchecked")
            BlockEntityTicker<T> ticker = (BlockEntityTicker<T>)state.getTicker(level, target.getType());
            if (ticker == null) {
                return;
            }
            ticker.tick(level, pos, state, target);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Speed", speed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        speed = tag.contains("Speed") ? Math.clamp(tag.getInt("Speed"), 0, 15) : 1;
    }
}
