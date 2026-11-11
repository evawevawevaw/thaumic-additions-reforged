package thaumicadditions.item;

import com.mojang.logging.LogUtils;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.registry.ModSounds;

public final class BlockTransport {
    private BlockTransport() {
    }

    public static boolean capture(ServerLevel level, BlockPos pos, ServerPlayer player, ItemStack stack) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || stack.getCount() != 1 || level.captureBlockSnapshots) {
            return false;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null && entity.onlyOpCanSetNbt() && !player.hasPermissions(2)) {
            return false;
        }
        if (NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, state, player)).isCanceled()) {
            return false;
        }
        if (level.getBlockState(pos) != state || level.getBlockEntity(pos) != entity) {
            return false;
        }
        CustomData data = entity == null ? CustomData.EMPTY : CustomData.of(entity.saveWithFullMetadata(level.registryAccess()));
        TransportedBlock cargo = new TransportedBlock(state, data);
        boolean removed = stage(level, () -> {
            if (entity != null) {
                level.getChunkAt(pos).removeBlockEntity(pos);
            }
            try {
                if (level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL) && level.getBlockState(pos).isAir()) {
                    return true;
                }
            } catch (RuntimeException exception) {
                LogUtils.getLogger().warn("Failed to capture block at {}", pos, exception);
            }
            restore(level, pos, state, entity);
            return false;
        });
        if (!removed) {
            return false;
        }
        stack.set(ModDataComponents.TRANSPORTED_BLOCK.get(), cargo);
        commit(level, pos, state);
        level.playSound(null, pos, ModSounds.PORTALCLOSE.get(), SoundSource.BLOCKS, 1, 1);
        return true;
    }

    public static boolean place(ServerLevel level, BlockPos pos, net.minecraft.core.Direction side,
        ServerPlayer player, ItemStack stack, TransportedBlock cargo) {
        if (!level.isEmptyBlock(pos) || cargo.state().isAir() || stack.getCount() != 1 || level.captureBlockSnapshots) {
            return false;
        }
        BlockSnapshot previous = BlockSnapshot.create(level.dimension(), level, pos);
        boolean placed = stage(level, () -> {
            try {
                if (level.setBlock(pos, cargo.state(), Block.UPDATE_ALL) && level.getBlockState(pos) == cargo.state()) {
                    BlockEntity entity = level.getBlockEntity(pos);
                    CompoundTag data = cargo.blockEntity().copyTag();
                    if (!data.isEmpty()) {
                        if (entity == null || entity.onlyOpCanSetNbt() && !player.hasPermissions(2)
                            || !data.getString("id").equals(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(entity.getType()).toString())
                            || !entity.getType().isValid(cargo.state())) {
                            restore(level, pos, previous.getState(), null);
                            return false;
                        }
                        data.putInt("x", pos.getX());
                        data.putInt("y", pos.getY());
                        data.putInt("z", pos.getZ());
                        entity.loadWithComponents(data, level.registryAccess());
                        entity.setChanged();
                        level.invalidateCapabilities(pos);
                    }
                    return true;
                }
            } catch (RuntimeException exception) {
                LogUtils.getLogger().warn("Failed to restore transported block at {}", pos, exception);
            }
            restore(level, pos, previous.getState(), null);
            return false;
        });
        if (!placed) {
            return false;
        }
        boolean rejected;
        try {
            rejected = EventHooks.onBlockPlace(player, previous, side) || level.getBlockState(pos) != cargo.state();
        } catch (RuntimeException exception) {
            LogUtils.getLogger().warn("Placement event failed for transported block at {}", pos, exception);
            rejected = true;
        }
        if (rejected) {
            stage(level, () -> {
                restore(level, pos, previous.getState(), null);
                return false;
            });
            level.sendBlockUpdated(pos, cargo.state(), previous.getState(), Block.UPDATE_CLIENTS);
            return false;
        }
        stack.remove(ModDataComponents.TRANSPORTED_BLOCK.get());
        commit(level, pos, previous.getState());
        level.playSound(null, pos, ModSounds.PORTALOPEN.get(), SoundSource.BLOCKS, 1, 1);
        return true;
    }

    private static void restore(ServerLevel level, BlockPos pos, BlockState state, BlockEntity entity) {
        level.getChunkAt(pos).removeBlockEntity(pos);
        if (level.getBlockState(pos) != state) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
        if (level.getBlockState(pos) != state) {
            throw new IllegalStateException("Unable to roll back block transport at " + pos);
        }
        if (entity != null) {
            entity.clearRemoved();
            level.setBlockEntity(entity);
            entity.setChanged();
        }
    }

    private static boolean stage(ServerLevel level, Supplier<Boolean> action) {
        int snapshots = level.capturedBlockSnapshots.size();
        boolean capturing = level.captureBlockSnapshots;
        level.captureBlockSnapshots = true;
        try {
            return action.get();
        } finally {
            level.captureBlockSnapshots = capturing;
            level.capturedBlockSnapshots.subList(snapshots, level.capturedBlockSnapshots.size()).clear();
        }
    }

    private static void commit(ServerLevel level, BlockPos pos, BlockState previous) {
        BlockState state = level.getBlockState(pos);
        state.onPlace(level, pos, previous, false);
        level.markAndNotifyBlock(pos, level.getChunkAt(pos), previous, state, Block.UPDATE_ALL, 512);
    }
}
