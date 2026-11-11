package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.block.MagicStorageBlock;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicTransfer;
import thaumicadditions.registry.ModBlockEntities;

public final class MagicTankBlockEntity extends MagicBufferBlockEntity {
    public MagicTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAGIC_TANK.get(), pos, state);
    }

    public boolean reinforced() {
        return getBlockState().getBlock() instanceof MagicStorageBlock block && block.reinforced();
    }

    @Override
    public int capacity() {
        return reinforced() ? 2000 : 1000;
    }

    @Override
    protected int storageLimit() {
        return capacity() + 5;
    }

    @Override
    protected int insertionLimit() {
        return level != null && level.isLoaded(worldPosition.above())
            && level.getBlockEntity(worldPosition.above()) instanceof MagicTankBlockEntity ? storageLimit() : capacity();
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (!reinforced() && taint > capacity() / 2 && level.random.nextInt(1000) < 100) {
            level.removeBlock(worldPosition, false);
            level.explode(null, worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5,
                1.0F, Level.ExplosionInteraction.NONE);
            spreadFlux();
            return;
        }
        if (level.isLoaded(worldPosition.below())
            && level.getBlockEntity(worldPosition.below()) instanceof MagicTankBlockEntity below
            && (!reinforced() || below.reinforced())) {
            for (int amount = 5; amount > 0; amount--) {
                for (MagicKind kind : MagicKind.values()) {
                    if (stored(kind) >= amount && below.totalStored() + amount <= below.capacity()) {
                        MagicTransfer.move(this, below, kind, amount, Direction.DOWN);
                    }
                }
            }
        }
        if (totalStored() > capacity() && level.isLoaded(worldPosition.above())
            && level.getBlockEntity(worldPosition.above()) instanceof MagicTankBlockEntity above
            && (!reinforced() || above.reinforced())) {
            MagicKind dominant = vis >= taint ? MagicKind.VIS : MagicKind.TAINT;
            MagicTransfer.move(this, above, dominant, stored(dominant), Direction.UP);
        }
        syncIfNeeded();
    }

    private void spreadFlux() {
        int placed = 0;
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-4, -4, -4), worldPosition.offset(4, 4, 4))) {
            if (level.isLoaded(pos) && level.isEmptyBlock(pos) && level.random.nextInt(100) < 25) {
                BlockState flux = level.random.nextInt(100) < 75
                    ? thaumcraft.registry.ModBlocks.FLUX_GAS.get().defaultBlockState()
                    : thaumcraft.registry.ModBlocks.FLUX_GOO.get().defaultBlockState();
                level.setBlock(pos, flux, 3);
                if (++placed >= 40) {
                    return;
                }
            }
        }
    }
}
