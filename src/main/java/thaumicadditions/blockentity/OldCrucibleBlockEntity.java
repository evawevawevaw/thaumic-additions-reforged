package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.aspect.AspectHelper;
import thaumcraft.aspect.AspectList;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicPort;
import thaumicadditions.magic.MagicTransfer;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModSounds;

public final class OldCrucibleBlockEntity extends MagicBufferBlockEntity {
    private int cooldown;

    public OldCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OLD_CRUCIBLE.get(), pos, state);
    }

    @Override
    public int capacity() {
        return 250;
    }

    @Override
    public boolean canConnect(Direction side) {
        return side.getAxis().isHorizontal();
    }

    @Override
    public int insert(MagicKind kind, int amount, Direction side, boolean simulate) {
        return 0;
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
            setChanged();
        }
        if (cooldown == 0) {
            absorbItem();
        }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos neighbour = worldPosition.relative(side);
            if (level.isLoaded(neighbour) && level.getBlockEntity(neighbour) instanceof MagicPort target) {
                for (MagicKind kind : MagicKind.values()) {
                    MagicTransfer.move(this, target, kind, stored(kind), side);
                }
            }
        }
        syncIfNeeded();
    }

    private void absorbItem() {
        AABB area = new AABB(worldPosition.getX(), worldPosition.getY() + 0.1, worldPosition.getZ(),
            worldPosition.getX() + 1, worldPosition.getY() + 1.1, worldPosition.getZ() + 1);
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, area, item -> item.isAlive() && !item.getItem().isEmpty())) {
            ItemStack stack = entity.getItem();
            AspectList aspects = AspectHelper.getObjectTags(stack);
            if (aspects == null || aspects.isEmpty()) {
                continue;
            }
            int converted = AspectHelper.reduceToPrimals(aspects).visSize() / 2;
            if (converted <= 0 || converted > capacity() - totalStored()) {
                continue;
            }
            vis += converted / 2;
            taint += converted / 2;
            cooldown = 40;
            stack.shrink(1);
            if (stack.isEmpty()) {
                entity.discard();
            }
            level.playSound(null, worldPosition, ModSounds.BUBBLING.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            changed();
            return;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("cooldown", cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        cooldown = Math.clamp(tag.getInt("cooldown"), 0, 40);
    }
}
