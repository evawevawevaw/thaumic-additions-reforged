package thaumicadditions.magic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import thaumicadditions.blockentity.VisPipeBlockEntity;

public final class PipeNetwork {
    public static final int CAPACITY = 16;
    private static final Map<Level, Map<BlockPos, PipeNetwork>> NETWORKS = new HashMap<>();
    private final List<VisPipeBlockEntity> pipes;
    private long tickedAt = Long.MIN_VALUE;

    private PipeNetwork(List<VisPipeBlockEntity> pipes) {
        this.pipes = pipes;
    }

    public static PipeNetwork get(VisPipeBlockEntity origin) {
        Level level = origin.getLevel();
        Map<BlockPos, PipeNetwork> cache = NETWORKS.computeIfAbsent(level, ignored -> new HashMap<>());
        PipeNetwork existing = cache.get(origin.getBlockPos());
        if (existing != null) {
            return existing;
        }
        List<VisPipeBlockEntity> members = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<VisPipeBlockEntity> queue = new ArrayDeque<>();
        queue.add(origin);
        visited.add(origin.getBlockPos());
        while (!queue.isEmpty()) {
            VisPipeBlockEntity pipe = queue.removeFirst();
            members.add(pipe);
            for (Direction side : Direction.values()) {
                BlockPos neighbour = pipe.getBlockPos().relative(side);
                if (pipe.canConnect(side) && level.isLoaded(neighbour)
                    && level.getBlockEntity(neighbour) instanceof VisPipeBlockEntity other
                    && !other.isRemoved() && other.canConnect(side.getOpposite()) && visited.add(neighbour)) {
                    queue.addLast(other);
                }
            }
        }
        members.sort(Comparator.comparingLong(pipe -> pipe.getBlockPos().asLong()));
        PipeNetwork network = new PipeNetwork(List.copyOf(members));
        for (VisPipeBlockEntity pipe : members) {
            cache.put(pipe.getBlockPos(), network);
        }
        return network;
    }

    public static void invalidate(Level level) {
        NETWORKS.remove(level);
    }

    public static void clearAll() {
        NETWORKS.clear();
    }

    public int stored(MagicKind kind) {
        return pipes.stream().mapToInt(pipe -> pipe.localStored(kind)).sum();
    }

    public int insert(VisPipeBlockEntity origin, MagicKind kind, int requested, boolean simulate) {
        int accepted = Math.min(Math.max(0, requested), Math.max(0, CAPACITY - stored(MagicKind.VIS) - stored(MagicKind.TAINT)));
        if (!simulate && accepted > 0) {
            origin.setLocal(kind, origin.localStored(kind) + accepted);
        }
        return accepted;
    }

    public int extract(MagicKind kind, int requested, boolean simulate) {
        int amount = Math.min(Math.max(0, requested), stored(kind));
        if (!simulate) {
            int remaining = amount;
            for (VisPipeBlockEntity pipe : pipes) {
                int take = Math.min(remaining, pipe.localStored(kind));
                pipe.setLocal(kind, pipe.localStored(kind) - take);
                remaining -= take;
                if (remaining == 0) {
                    break;
                }
            }
        }
        return amount;
    }

    public void tick(Level level) {
        if (tickedAt == level.getGameTime()) {
            return;
        }
        tickedAt = level.getGameTime();
        for (VisPipeBlockEntity pipe : pipes) {
            if (pipe.filter() && stored(MagicKind.TAINT) > 0 && level.random.nextInt(100) < 10) {
                extract(MagicKind.TAINT, 1, false);
            }
        }
        for (VisPipeBlockEntity pipe : pipes) {
            for (Direction side : Direction.values()) {
                BlockPos neighbour = pipe.getBlockPos().relative(side);
                if (pipe.canConnect(side) && level.isLoaded(neighbour)
                    && level.getBlockEntity(neighbour) instanceof MagicPort destination
                    && !(destination instanceof VisPipeBlockEntity)) {
                    for (MagicKind kind : MagicKind.values()) {
                        MagicTransfer.move(pipe, destination, kind, stored(kind), side);
                    }
                }
            }
        }
        distribute();
        int vis = stored(MagicKind.VIS);
        int taint = stored(MagicKind.TAINT);
        for (VisPipeBlockEntity pipe : pipes) {
            pipe.updateDisplay(vis, taint);
        }
    }

    private void distribute() {
        int vis = stored(MagicKind.VIS);
        int taint = stored(MagicKind.TAINT);
        List<MagicDistribution.Allocation> allocations = MagicDistribution.balance(vis, taint, pipes.size(), CAPACITY);
        for (int i = 0; i < pipes.size(); i++) {
            MagicDistribution.Allocation allocation = allocations.get(i);
            pipes.get(i).setLocalCounts(allocation.vis(), allocation.taint());
        }
    }
}
