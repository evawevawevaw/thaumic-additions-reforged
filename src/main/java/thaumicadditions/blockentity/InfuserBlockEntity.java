package thaumicadditions.blockentity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import thaumicadditions.block.InfuserBlock;
import thaumicadditions.crafting.InfuserRecipe;
import thaumicadditions.crafting.InfuserRecipes;
import thaumicadditions.event.InfuserCraftedEvent;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicPort;
import thaumicadditions.menu.InfuserMenu;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModSounds;

public final class InfuserBlockEntity extends InventoryMachineBlockEntity implements MagicPort {
    private int visProgress;
    private int taintProgress;
    private int visCost = 47;
    private int taintCost = 47;
    private String activeRecipe = "";
    private final ContainerData progress = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> visProgress;
                case 1 -> taintProgress;
                case 2 -> visCost;
                case 3 -> taintCost;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> visProgress = value;
                case 1 -> taintProgress = value;
                case 2 -> visCost = value;
                case 3 -> taintCost = value;
                default -> { }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public InfuserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSER.get(), pos, state, isDark(state) ? 6 : 8);
    }

    private static boolean isDark(BlockState state) {
        return state.getBlock() instanceof InfuserBlock block && block.dark();
    }

    public boolean dark() {
        return isDark(getBlockState());
    }

    public ContainerData progressData() {
        return progress;
    }

    public boolean working() {
        return visProgress + taintProgress > 0;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new InfuserMenu(id, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot >= 1 && slot <= (dark() ? 5 : 6);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return dark() ? new int[]{0} : new int[]{0, 7};
        }
        return dark() ? new int[]{1, 2, 3, 4, 5} : new int[]{1, 2, 3, 4, 5, 6};
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        InfuserRecipe recipe = InfuserRecipes.find(dark(), this);
        if (recipe == null) {
            if (!activeRecipe.isEmpty()) {
                resetWork();
                changed();
            }
            syncIfNeeded();
            return;
        }
        if (!recipe.key().equals(activeRecipe)) {
            resetWork();
            activeRecipe = recipe.key();
            visCost = recipe.visCost();
            taintCost = recipe.taintCost();
            changed();
        }
        visCost = recipe.visCost();
        taintCost = recipe.taintCost();
        visProgress = Math.min(visProgress, visCost);
        taintProgress = Math.min(taintProgress, taintCost);
        if (!canOutput(recipe)) {
            syncIfNeeded();
            return;
        }
        if (visProgress < visCost || taintProgress < taintCost) {
            if (level.random.nextInt(100) < 25) {
                boolean gained = false;
                if (visProgress < visCost) {
                    boolean drawn = drawOne(MagicKind.VIS);
                    visProgress = drawn ? visProgress + 1 : Math.max(0, visProgress - 1);
                    gained |= drawn;
                }
                if (taintProgress < taintCost) {
                    boolean drawn = drawOne(MagicKind.TAINT);
                    taintProgress = drawn ? taintProgress + 1 : Math.max(0, taintProgress - 1);
                    gained |= drawn;
                }
                if (gained && (!dark() || visProgress > 10 || taintProgress > 10)) {
                    level.playSound(null, worldPosition, ModSounds.INFUSER.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
                }
                changed();
            }
        }
        if (visProgress >= visCost && taintProgress >= taintCost) {
            finish(recipe);
        }
        syncIfNeeded();
    }

    private boolean drawOne(MagicKind kind) {
        for (Direction side : Direction.values()) {
            BlockPos neighbour = worldPosition.relative(side);
            if (canConnect(side) && level.isLoaded(neighbour) && level.getBlockEntity(neighbour) instanceof MagicPort source
                && source.canConnect(side.getOpposite()) && source.extract(kind, 1, side.getOpposite(), true) == 1
                && source.extract(kind, 1, side.getOpposite(), false) == 1) {
                return true;
            }
        }
        return false;
    }

    private boolean canOutput(InfuserRecipe recipe) {
        ItemStack result = recipe.createResult();
        if (result.isEmpty() || !fits(items.get(0), result)) {
            return false;
        }
        return !recipe.depletedShard() || fits(items.get(7), new ItemStack(ModItems.DEPLETED_SHARD.get()));
    }

    private static boolean fits(ItemStack existing, ItemStack addition) {
        return existing.isEmpty() ? addition.getCount() <= addition.getMaxStackSize() :
            ItemStack.isSameItemSameComponents(existing, addition)
                && addition.getCount() <= existing.getMaxStackSize() - existing.getCount();
    }

    private void finish(InfuserRecipe recipe) {
        ItemStack result = recipe.createResult();
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 1; i <= recipe.inputs().size(); i++) {
            inputs.add(items.get(i).copy());
        }
        for (int i = 1; i <= recipe.inputs().size(); i++) {
            if (!items.get(i).isEmpty()) {
                items.get(i).shrink(1);
            }
        }
        if (items.get(0).isEmpty()) {
            items.set(0, result.copy());
        } else {
            items.get(0).grow(result.getCount());
        }
        if (recipe.depletedShard() && level.random.nextBoolean()) {
            if (items.get(7).isEmpty()) {
                items.set(7, new ItemStack(ModItems.DEPLETED_SHARD.get()));
            } else {
                items.get(7).grow(1);
            }
        }
        resetWork();
        changed();
        NeoForge.EVENT_BUS.post(new InfuserCraftedEvent(this, recipe, inputs, result));
    }

    private void resetWork() {
        activeRecipe = "";
        visProgress = 0;
        taintProgress = 0;
        visCost = 0;
        taintCost = 0;
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
        return 0;
    }

    @Override
    public int extract(MagicKind kind, int amount, Direction side, boolean simulate) {
        return 0;
    }

    private void saveProgress(CompoundTag tag) {
        tag.putString("recipe", activeRecipe);
        tag.putInt("vis_progress", visProgress);
        tag.putInt("taint_progress", taintProgress);
        tag.putInt("vis_cost", visCost);
        tag.putInt("taint_cost", taintCost);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveProgress(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        activeRecipe = tag.getString("recipe");
        visCost = Math.clamp(tag.getInt("vis_cost"), 0, 32767);
        taintCost = Math.clamp(tag.getInt("taint_cost"), 0, 32767);
        visProgress = Math.clamp(tag.getInt("vis_progress"), 0, visCost);
        taintProgress = Math.clamp(tag.getInt("taint_progress"), 0, taintCost);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveProgress(tag);
        return tag;
    }
}
