package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.registry.ModBlockEntities;

public final class ReturnerBlockEntity extends BlockEntity {
    private GlobalPos destination;

    public ReturnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RETURNER.get(), pos, state);
    }

    public GlobalPos destination() {
        return destination == null ? GlobalPos.of(level == null ? Level.OVERWORLD : level.dimension(), worldPosition) : destination;
    }

    public void setDestination(GlobalPos destination) {
        this.destination = destination;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        GlobalPos target = destination();
        tag.putString("dimension", target.dimension().location().toString());
        tag.putLong("destination", target.pos().asLong());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ResourceLocation dimension = ResourceLocation.tryParse(tag.getString("dimension"));
        destination = dimension == null ? null : GlobalPos.of(ResourceKey.create(Registries.DIMENSION, dimension),
            BlockPos.of(tag.getLong("destination")));
    }
}
