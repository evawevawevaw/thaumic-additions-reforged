package thaumicadditions.blockentity;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.fx.Fx;
import thaumcraft.infusion.InfusionMatrixBlockEntity;
import thaumcraft.wand.Wands;
import thaumicadditions.machine.ActivatorMotion;
import thaumicadditions.registry.ModBlockEntities;

public final class InfusionActivatorBlockEntity extends BlockEntity {
    private ItemStack wand = ItemStack.EMPTY;
    private UUID owner;
    private int phase;
    private boolean rising = true;

    public InfusionActivatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSION_ACTIVATOR.get(), pos, state);
    }

    public void setOwner(Player player) {
        owner = player.getUUID();
        setChanged();
    }

    public boolean hasWand() {
        return !wand.isEmpty();
    }

    public ItemStack displayedWand() {
        return wand.copy();
    }

    public float phase(float partialTick) {
        if (wand.isEmpty()) {
            return phase;
        }
        int speed = Math.max(1, (int)(Math.sqrt(Wands.getRod(wand).capacity()) / 5));
        return Math.clamp(phase + (rising ? speed : -speed) * partialTick, 0, 100);
    }

    public boolean install(ItemStack stack, Player player) {
        if (level == null || level.isClientSide || hasWand() || !Wands.isWand(stack)) {
            return false;
        }
        wand = stack.copyWithCount(1);
        if (owner == null) {
            owner = player.getUUID();
        }
        phase = 0;
        rising = true;
        sync();
        return true;
    }

    public ItemStack removeWand() {
        if (level == null || level.isClientSide) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = wand;
        wand = ItemStack.EMPTY;
        phase = 0;
        rising = true;
        sync();
        return removed;
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public void tick() {
        if (level == null || wand.isEmpty()) {
            return;
        }
        ActivatorMotion.Step next = ActivatorMotion.advance(phase, rising, Wands.getRod(wand).capacity());
        if (level instanceof ServerLevel server && next.activate() && owner != null) {
            ServerPlayer player = server.getServer().getPlayerList().getPlayer(owner);
            BlockPos matrixPos = worldPosition.below();
            if (player != null && player.level() == server && !player.isSpectator()
                && server.mayInteract(player, matrixPos) && player.mayUseItemAt(matrixPos, Direction.UP, wand)
                && server.isLoaded(matrixPos) && server.getBlockEntity(matrixPos) instanceof InfusionMatrixBlockEntity matrix) {
                matrix.onWandUseOn(server, matrixPos, player, wand, Direction.UP);
            }
        }
        if (level.isClientSide && (next.activate() || next.peak())) {
            float x = worldPosition.getX();
            float y = worldPosition.getY();
            float z = worldPosition.getZ();
            float sourceY = y + phase / 360.0F + (next.peak() ? 0.83F : 0.2F);
            for (float[] target : new float[][]{{0.1F, 0.1F}, {0.1F, 1}, {1, 1}, {1, 0.1F}}) {
                Fx.get().nodeBolt(level, x + 0.5F, sourceY, z + 0.5F, x + target[0], y + 0.6F, z + target[1]);
            }
        }
        phase = next.phase();
        rising = next.rising();
        if (!level.isClientSide) {
            setChanged();
            if (level.getGameTime() % 20 == 0) {
                sync();
            }
        }
    }

    private void writeDisplay(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("wand", wand.saveOptional(registries));
        tag.putInt("phase", phase);
        tag.putBoolean("rising", rising);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        writeDisplay(tag, registries);
        if (owner != null) {
            tag.putUUID("owner", owner);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        wand = ItemStack.parseOptional(registries, tag.getCompound("wand"));
        if (!wand.isEmpty() && Wands.isWand(wand)) {
            wand.setCount(1);
        } else {
            wand = ItemStack.EMPTY;
        }
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        phase = Math.clamp(tag.getInt("phase"), 0, 100);
        rising = !tag.contains("rising") || tag.getBoolean("rising");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        writeDisplay(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
