package thaumicadditions.world;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import thaumcraft.registry.EldritchContent;
import thaumicadditions.block.EldritchLockBlock;
import thaumicadditions.block.EldritchStoneBlock;
import thaumicadditions.blockentity.VoidChestBlockEntity;
import thaumicadditions.registry.ModBlocks;

public final class MonolithStructure {
    private MonolithStructure() {
    }

    public static Map<BlockPos, BlockState> plan(BlockPos origin, boolean breakable, boolean chests) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        BlockState stone = (breakable ? ModBlocks.ELDRITCH_STONE : ModBlocks.UNBREAKABLE_ELDRITCH_STONE).get().defaultBlockState();
        fill(plan, origin, -3, -20, -3, 3, 0, 3, stone);
        fill(plan, origin, -10, -31, -10, 10, -21, 10, stone);
        fill(plan, origin, -3, -29, -3, 3, -25, 3, Blocks.AIR.defaultBlockState());
        plan.put(origin.above(3), EldritchContent.ELDRITCH_OBELISK.get().defaultBlockState());
        plan.put(origin, ModBlocks.ELDRITCH_LOCK.get().defaultBlockState());
        if (chests) {
            for (int x : new int[]{-2, 2}) {
                for (int z : new int[]{-2, 2}) {
                    plan.put(origin.offset(x, -28, z), ModBlocks.VOID_CHEST.get().defaultBlockState());
                }
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            plan.put(origin.below(28).relative(direction, 4), ModBlocks.ELDRITCH_LOCK.get().defaultBlockState()
                .setValue(EldritchLockBlock.WALL, true));
        }
        return plan;
    }

    public static Map<BlockPos, BlockState> entrance(BlockPos origin) {
        Map<BlockPos, BlockState> result = plan(origin, true, false);
        fill(result, origin, 0, -25, 0, 0, 0, 0, Blocks.AIR.defaultBlockState());
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            result.put(origin.above().relative(direction), Blocks.AIR.defaultBlockState());
        }
        return result;
    }

    public static Map<BlockPos, BlockState> room(BlockPos lock, Direction inside) {
        Map<BlockPos, BlockState> result = new LinkedHashMap<>();
        Direction lateral = inside.getClockWise();
        for (int depth = 1; depth <= 5; depth++) {
            for (int y = -2; y <= 4; y++) {
                for (int width = -2; width <= 2; width++) {
                    result.put(lock.relative(inside, depth).relative(lateral, width).above(y), Blocks.AIR.defaultBlockState());
                }
            }
        }
        for (int y = -1; y <= 1; y++) {
            for (int width = -1; width <= 1; width++) {
                result.put(lock.relative(lateral, width).above(y), Blocks.AIR.defaultBlockState());
            }
        }
        result.put(lock.relative(inside, 3), EldritchContent.ELDRITCH_OBELISK.get().defaultBlockState());
        result.put(lock.relative(inside, 3).below(2), ModBlocks.VOID_CHEST.get().defaultBlockState());
        return result;
    }

    private static void fill(Map<BlockPos, BlockState> plan, BlockPos origin, int x1, int y1, int z1,
        int x2, int y2, int z2, BlockState state) {
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(x1, y1, z1), origin.offset(x2, y2, z2))) {
            plan.put(pos.immutable(), state);
        }
    }

    public static boolean apply(LevelAccessor level, Map<BlockPos, BlockState> requested, ServerPlayer player, boolean loot) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        for (var entry : requested.entrySet()) {
            BlockPos pos = entry.getKey();
            if (level.isOutsideBuildHeight(pos) || !level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                return false;
            }
            BlockState old = level.getBlockState(pos);
            if (old.is(ModBlocks.VOID_CHEST.get()) || old.equals(entry.getValue())) {
                continue;
            }
            if (player == null && level.getBlockEntity(pos) != null) {
                return false;
            }
            if (player != null && (!player.level().mayInteract(player, pos)
                || !player.mayUseItemAt(pos, Direction.UP, player.getMainHandItem())
                || level.getBlockEntity(pos) != null && !(old.getBlock() instanceof thaumcraft.device.CrystalClusterBlock)
                || old.getDestroySpeed(level, pos) < 0 && !(old.getBlock() instanceof EldritchStoneBlock)
                    && !(old.getBlock() instanceof EldritchLockBlock))) {
                return false;
            }
            if (player != null && !old.isAir()
                && NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(player.level(), pos, old, player)).isCanceled()) {
                return false;
            }
            plan.put(pos, entry.getValue());
        }
        List<BlockSnapshot> snapshots = new ArrayList<>();
        for (var entry : plan.entrySet()) {
            if (player != null) {
                snapshots.add(BlockSnapshot.create(player.level().dimension(), level, entry.getKey()));
            }
            if (!level.setBlock(entry.getKey(), entry.getValue(), Block.UPDATE_CLIENTS)) {
                if (!level.getBlockState(entry.getKey()).equals(entry.getValue())) {
                    restore(snapshots);
                    return false;
                }
            }
        }
        if (player != null) {
            for (BlockSnapshot snapshot : snapshots) {
                if (EventHooks.onBlockPlace(player, snapshot, Direction.UP)) {
                    restore(snapshots);
                    return false;
                }
            }
        }
        for (var entry : plan.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof EldritchStoneBlock) {
                level.setBlock(pos, EldritchStoneBlock.connected(state, level, pos), Block.UPDATE_CLIENTS);
            }
            if (loot && level.getBlockEntity(pos) instanceof VoidChestBlockEntity chest) {
                chest.generateLoot();
            }
            if (level instanceof ServerLevel server) {
                server.updateNeighborsAt(pos, state.getBlock());
            }
        }
        return true;
    }

    private static void restore(List<BlockSnapshot> snapshots) {
        for (int index = snapshots.size() - 1; index >= 0; index--) {
            snapshots.get(index).restore(Block.UPDATE_ALL);
        }
    }
}
