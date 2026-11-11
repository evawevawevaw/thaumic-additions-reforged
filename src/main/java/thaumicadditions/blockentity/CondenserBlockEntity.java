package thaumicadditions.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thaumicadditions.item.UpgradeItem;
import thaumicadditions.machine.CondenserCycle;
import thaumicadditions.machine.Upgradable;
import thaumicadditions.machine.UpgradeType;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicPort;
import thaumicadditions.menu.CondenserMenu;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModItems;

public final class CondenserBlockEntity extends InventoryMachineBlockEntity implements MagicPort, Upgradable {
    private final NonNullList<ItemStack> upgrades = NonNullList.withSize(2, ItemStack.EMPTY);
    private int remaining = -1;
    private int baseColor = 0xFFFFFF;
    private int vis;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> remaining;
                case 1 -> crystalColor() & 65535;
                case 2 -> crystalColor() >>> 16;
                case 3 -> vis & 65535;
                case 4 -> vis >>> 16;
                case 5, 6 -> upgradeIndex(index - 5);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public CondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONDENSER.get(), pos, state, 2);
    }

    public ContainerData progressData() {
        return data;
    }

    public int remaining() {
        return remaining;
    }

    public int crystalColor() {
        double fade = Math.pow(23999.0 / 24000.0, CondenserCycle.LIFETIME - Math.max(0, remaining));
        return (int)Math.round((baseColor >> 16 & 255) * fade) << 16
            | (int)Math.round((baseColor >> 8 & 255) * fade) << 8 | (int)Math.round((baseColor & 255) * fade);
    }

    public int countUpgrades(UpgradeType type) {
        int count = 0;
        for (ItemStack stack : upgrades) {
            if (stack.getItem() instanceof UpgradeItem upgrade && upgrade.type() == type) {
                count++;
            }
        }
        return count;
    }

    private int upgradeIndex(int slot) {
        return upgrades.get(slot).getItem() instanceof UpgradeItem item ? item.type().ordinal() : -1;
    }

    public static int shardColor(ItemStack stack) {
        if (stack.is(thaumcraft.registry.ModItems.AIR_SHARD.get())) return 0xFFFF7E;
        if (stack.is(thaumcraft.registry.ModItems.FIRE_SHARD.get())) return 0xFF3C01;
        if (stack.is(thaumcraft.registry.ModItems.WATER_SHARD.get())) return 0x0090FF;
        if (stack.is(thaumcraft.registry.ModItems.EARTH_SHARD.get())) return 0x00A000;
        if (stack.is(thaumcraft.registry.ModItems.ORDER_SHARD.get())) return 0xEECCFF;
        if (stack.is(thaumcraft.registry.ModItems.ENTROPY_SHARD.get())) return 0x555577;
        return stack.is(thaumcraft.registry.ModItems.BALANCED_SHARD.get()) ? 0xFFFFFF : -1;
    }

    @Override
    public void serverTick() {
        if (level == null || level.isClientSide) {
            return;
        }
        for (int step = 0, steps = 1 + countUpgrades(UpgradeType.SPEED); step < steps; step++) {
            if (remaining >= 0) {
                ItemStack depleted = new ItemStack(ModItems.DEPLETED_SHARD.get());
                ItemStack output = items.get(1);
                boolean outputReady = output.isEmpty() || ItemStack.isSameItemSameComponents(output, depleted)
                    && output.getCount() < Math.min(getMaxStackSize(), output.getMaxStackSize());
                CondenserCycle.Step result = CondenserCycle.advance(remaining, countUpgrades(UpgradeType.EFFICIENCY),
                    outputReady, vis < Integer.MAX_VALUE);
                if (result.remaining() != remaining) {
                    remaining = result.remaining();
                    changed();
                }
                if (result.generated()) {
                    vis++;
                    changed();
                }
                if (result.expired()) {
                    if (output.isEmpty()) {
                        items.set(1, depleted);
                    } else {
                        output.grow(1);
                    }
                    changed();
                }
            }
            exportVis();
            if (remaining < 0 && shardColor(items.get(0)) >= 0) {
                baseColor = shardColor(items.get(0));
                remaining = CondenserCycle.LIFETIME;
                items.get(0).shrink(1);
                changed();
            }
        }
        syncIfNeeded();
    }

    private void exportVis() {
        if (vis <= 0) {
            return;
        }
        for (Direction side : Direction.values()) {
            BlockPos target = worldPosition.relative(side);
            if (canConnect(side) && level.isLoaded(target) && level.getBlockEntity(target) instanceof MagicPort port
                && port.canConnect(side.getOpposite())) {
                int accepted = Math.clamp(port.insert(MagicKind.VIS, 1, side.getOpposite(), false), 0, 1);
                if (accepted > 0) {
                    vis -= accepted;
                    changed();
                    return;
                }
            }
        }
    }

    @Override
    public boolean canInstall(UpgradeType type) {
        return (type == UpgradeType.SPEED || type == UpgradeType.EFFICIENCY) && upgrades.stream().anyMatch(ItemStack::isEmpty);
    }

    @Override
    public boolean installUpgrade(ItemStack stack) {
        if (level == null || level.isClientSide || !(stack.getItem() instanceof UpgradeItem item) || !canInstall(item.type())) {
            return false;
        }
        for (int slot = 0; slot < upgrades.size(); slot++) {
            if (upgrades.get(slot).isEmpty()) {
                upgrades.set(slot, stack.copyWithCount(1));
                changed();
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack removeUpgrade() {
        if (level == null || level.isClientSide) {
            return ItemStack.EMPTY;
        }
        for (int slot = upgrades.size() - 1; slot >= 0; slot--) {
            if (!upgrades.get(slot).isEmpty()) {
                ItemStack stack = upgrades.set(slot, ItemStack.EMPTY);
                changed();
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canConnect(Direction side) {
        return side != Direction.UP;
    }

    @Override
    public int stored(MagicKind kind) {
        return kind == MagicKind.VIS ? vis : 0;
    }

    @Override
    public int insert(MagicKind kind, int amount, Direction side, boolean simulate) {
        return 0;
    }

    @Override
    public int extract(MagicKind kind, int amount, Direction side, boolean simulate) {
        int extracted = kind == MagicKind.VIS && canConnect(side) ? Math.min(vis, Math.max(0, amount)) : 0;
        if (!simulate && extracted > 0) {
            vis -= extracted;
            changed();
        }
        return extracted;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && shardColor(stack) >= 0;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{1} : new int[]{0};
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CondenserMenu(id, inventory, this);
    }

    private void saveProgress(CompoundTag tag) {
        tag.putInt("remaining", remaining);
        tag.putInt("base_color", baseColor);
        tag.putInt("vis", vis);
        CompoundTag upgradeTag = new CompoundTag();
        for (int slot = 0; slot < 2; slot++) {
            upgradeTag.putInt("type" + slot, upgradeIndex(slot));
        }
        tag.put("upgrade_display", upgradeTag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveProgress(tag);
        CompoundTag upgradeTag = new CompoundTag();
        ContainerHelper.saveAllItems(upgradeTag, upgrades, registries);
        tag.put("upgrades", upgradeTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        remaining = tag.contains("remaining") ? Math.clamp(tag.getInt("remaining"), -1, CondenserCycle.LIFETIME) : -1;
        baseColor = tag.getInt("base_color") & 0xFFFFFF;
        vis = Math.max(0, tag.getInt("vis"));
        if (tag.contains("upgrades", Tag.TAG_COMPOUND)) {
            upgrades.clear();
            ContainerHelper.loadAllItems(tag.getCompound("upgrades"), upgrades, registries);
            for (int slot = 0; slot < 2; slot++) {
                ItemStack stack = upgrades.get(slot);
                if (!(stack.getItem() instanceof UpgradeItem item)
                    || item.type() != UpgradeType.SPEED && item.type() != UpgradeType.EFFICIENCY) {
                    upgrades.set(slot, ItemStack.EMPTY);
                } else {
                    stack.setCount(1);
                }
            }
        } else if (tag.contains("upgrade_display")) {
            CompoundTag display = tag.getCompound("upgrade_display");
            for (int slot = 0; slot < 2; slot++) {
                int type = display.getInt("type" + slot);
                upgrades.set(slot, type == 0 ? new ItemStack(ModItems.UPGRADE_SPEED.get()) :
                    type == 1 ? new ItemStack(ModItems.UPGRADE_EFFICIENCY.get()) : ItemStack.EMPTY);
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveProgress(tag);
        return tag;
    }
}
