package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.fx.Fx;
import thaumcraft.node.NodeBlockEntity;
import thaumicadditions.menu.HyperNodeCreatorMenu;
import thaumicadditions.registry.ModBlockEntities;

public final class HyperNodeCreatorBlockEntity extends InventoryMachineBlockEntity {
    private boolean primalsOnly;
    private boolean nodePresent;
    private int totalItems;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? (primalsOnly ? 1 : 0) | (nodePresent ? 2 : 0) : totalItems;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public HyperNodeCreatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HYPER_NODE_CREATOR.get(), pos, state, 36);
    }

    public ContainerData nodeData() {
        return data;
    }

    public boolean primalsOnly() {
        return primalsOnly;
    }

    public boolean nodePresent() {
        return nodePresent;
    }

    public void toggleMode() {
        if (level != null && !level.isClientSide) {
            primalsOnly = !primalsOnly;
            changed();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        int total = 0;
        for (var stack : items) {
            total += stack.getCount();
        }
        BlockPos above = worldPosition.above();
        NodeBlockEntity node = level.isLoaded(above) && level.getBlockEntity(above) instanceof NodeBlockEntity aura ? aura : null;
        if ((node != null) != nodePresent || total != totalItems) {
            nodePresent = node != null;
            totalItems = total;
            changed();
        }
        if (node != null) {
            AspectList aspects = new AspectList();
            if (total > 0) {
                for (Aspect aspect : Aspect.getPrimalAspects()) {
                    aspects.add(aspect, total);
                }
                if (!primalsOnly) {
                    for (Aspect aspect : Aspect.getCompoundAspects()) {
                        aspects.add(aspect, total);
                    }
                }
            }
            if (!aspects.equals(node.getAspects()) || !aspects.equals(node.getAspectsBase())) {
                node.setAspects(aspects);
                node.sync();
            }
        }
        syncIfNeeded();
    }

    public void clientTick() {
        if (level == null || !level.isClientSide || !nodePresent || !level.random.nextBoolean()) {
            return;
        }
        int corner = level.random.nextInt(8);
        float x = worldPosition.getX();
        float y = worldPosition.getY();
        float z = worldPosition.getZ();
        Fx.get().nodeBolt(level, x + (corner & 1), y + 1 + (corner >> 2 & 1), z + (corner >> 1 & 1),
            x + 0.5F, y + 1.5F, z + 0.5F);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return java.util.stream.IntStream.range(0, 36).toArray();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, net.minecraft.world.item.ItemStack stack, Direction side) {
        return true;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new HyperNodeCreatorMenu(id, inventory, this);
    }

    private void saveDisplay(CompoundTag tag) {
        tag.putBoolean("primals_only", primalsOnly);
        tag.putBoolean("node_present", nodePresent);
        tag.putInt("total_items", totalItems);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveDisplay(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        primalsOnly = tag.getBoolean("primals_only");
        nodePresent = tag.getBoolean("node_present");
        totalItems = Math.clamp(tag.getInt("total_items"), 0, 2304);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveDisplay(tag);
        return tag;
    }
}
