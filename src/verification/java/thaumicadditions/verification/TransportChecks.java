package thaumicadditions.verification;

import java.util.List;
import java.util.Random;
import net.minecraft.core.Direction;
import thaumicadditions.magic.MagicDistribution;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.magic.MagicPort;
import thaumicadditions.magic.MagicTransfer;

public final class TransportChecks {
    private TransportChecks() {
    }

    public static void main(String[] arguments) {
        checkTransfers();
        checkDistribution();
        checkCondenser();
        checkEnergy();
        checkMixedEnergy();
        checkFiniteFluid();
        checkActivator();
        checkAccelerationGuard();
        System.out.println("Machine checks passed: transport conservation, topology allocation, condenser lifetime and yield, rational energy conversion, mixed FE/mana conservation and reentrancy, finite fluid volume, activator motion, acceleration reentrancy");
    }

    private static void checkCondenser() {
        for (int speed = 0; speed <= 2; speed++) {
            for (int efficiency = 0; efficiency <= 2 - speed; efficiency++) {
                int remaining = thaumicadditions.machine.CondenserCycle.LIFETIME;
                int generated = 0;
                int steps = 0;
                while (remaining >= 0) {
                    var result = thaumicadditions.machine.CondenserCycle.advance(remaining, efficiency, true, true);
                    remaining = result.remaining();
                    generated += result.generated() ? 1 : 0;
                    steps++;
                }
                require(generated == (efficiency == 2 ? 48 : 24), "Incorrect per-shard condenser yield");
                require(steps == 24001, "Incorrect condenser lifetime");
                int ticks = (steps + speed) / (speed + 1);
                require(ticks == switch (speed) { case 1 -> 12001; case 2 -> 8001; default -> 24001; },
                    "Incorrect condenser speed scaling");
            }
        }
        var blockedOutput = thaumicadditions.machine.CondenserCycle.advance(0, 0, false, true);
        require(blockedOutput.remaining() == 0 && !blockedOutput.expired() && !blockedOutput.generated(),
            "Full output lost the depleted shard or generated extra vis");
        var blockedBuffer = thaumicadditions.machine.CondenserCycle.advance(23001, 0, true, false);
        require(blockedBuffer.remaining() == 23001 && !blockedBuffer.generated(), "Full buffer lost scheduled vis");
        var idle = thaumicadditions.machine.CondenserCycle.advance(-1, 2, true, true);
        require(!idle.generated() && !idle.expired(), "Idle condenser generated resources");
    }

    private static void checkEnergy() {
        var energy = new thaumicadditions.machine.VisEnergyBuffer();
        for (int i = 0; i < 6; i++) {
            require(energy.convertVis(), "Empty generator refused vis");
        }
        require(energy.energyStored() == 5000, "Fractional conversion lost energy");
        require(energy.extract(Integer.MAX_VALUE, true) == 2000 && energy.energyStored() == 5000,
            "Energy simulation mutated storage or exceeded transfer limit");
        require(energy.extract(Integer.MAX_VALUE, false) == 2000, "Incorrect generator transfer limit");
        require(energy.extract(4000, false) == 2000, "Incorrect second extraction");
        require(energy.extract(4000, false) == 1000 && energy.savedUnits() == 0, "Generator overdraft");
        energy.convertVis();
        require(energy.extract(Integer.MAX_VALUE, false) == 833 && energy.savedUnits() == 2,
            "Rational conversion remainder was lost");
        var restored = new thaumicadditions.machine.VisEnergyBuffer();
        restored.loadUnits(energy.savedUnits());
        for (int i = 0; i < 5; i++) {
            restored.convertVis();
        }
        require(restored.energyStored() == 4167, "Saved fractional remainder was lost");
        int consumed = 0;
        var full = new thaumicadditions.machine.VisEnergyBuffer();
        while (full.convertVis()) {
            consumed++;
        }
        require(full.energyStored() <= thaumicadditions.machine.VisEnergyBuffer.CAPACITY, "Energy overflow");
        int before = full.savedUnits();
        require(!full.convertVis() && full.savedUnits() == before && before == consumed * 5000,
            "Full generator created or lost energy");
        require(full.extract(-1, false) == 0 && full.savedUnits() == before, "Negative extraction changed storage");
    }

    private static void checkMixedEnergy() {
        var energy = new thaumicadditions.machine.VisEnergyBuffer();
        Random random = new Random(0xB07A);
        long supplied = 0;
        long exported = 0;
        for (int step = 0; step < 20000; step++) {
            switch (random.nextInt(3)) {
                case 0 -> {
                    if (energy.convertVis()) {
                        supplied += 5000;
                    }
                }
                case 1 -> exported += energy.extract(random.nextInt(4000), false) * 6L;
                default -> {
                    int accepted = energy.emitMana(offer -> {
                        require(offer > 0 && offer <= 2000, "Mana transfer exceeded the original per-side limit");
                        return random.nextInt(offer + 1);
                    });
                    exported += accepted * 60L;
                }
            }
            require(supplied == exported + energy.savedUnits(), "Mixed FE and mana export created or lost energy");
            require(energy.savedUnits() >= 0 && energy.energyStored() <= 640000, "Mixed export escaped storage bounds");
        }
        energy.loadUnits(640000 * 6);
        int accepted = energy.emitMana(offer -> {
            require(energy.emitMana(value -> value) == 0, "Reentrant mana export was allowed");
            require(!energy.convertVis(), "A receiver filled temporarily reserved capacity");
            return offer / 2;
        });
        require(accepted == 1000 && energy.energyStored() == 630000, "Partial mana acceptance was charged incorrectly");
        int before = energy.savedUnits();
        require(energy.emitMana(offer -> 0) == 0 && energy.savedUnits() == before, "A full mana receiver consumed energy");
    }

    private static void checkFiniteFluid() {
        Random random = new Random(910573L);
        for (int attempt = 0; attempt < 20000; attempt++) {
            int source = random.nextInt(9);
            int target = random.nextInt(9);
            int moved = thaumicadditions.machine.FiniteFluidFlow.downward(source, target);
            require(moved >= 0 && moved <= source && target + moved <= 8, "Invalid downward flow");
            require(source - moved + target + moved == source + target, "Downward flow lost volume");
            int[] adjacent = new int[4];
            int before = source;
            for (int index = 0; index < 4; index++) {
                adjacent[index] = random.nextInt(10) - 1;
                before += Math.max(0, adjacent[index]);
            }
            int[] result = thaumicadditions.machine.FiniteFluidFlow.balance(source, adjacent, random.nextInt(4));
            int after = result[0];
            require(result[0] >= 0 && result[0] <= 8, "Fluid center overflow");
            for (int index = 0; index < 4; index++) {
                require(result[index + 1] >= -1 && result[index + 1] <= 8, "Fluid neighbour overflow");
                if (adjacent[index] < 0 || adjacent[index] >= source - 1) {
                    require(result[index + 1] == adjacent[index], "Fluid entered a blocked or higher cell");
                }
                after += Math.max(0, result[index + 1]);
            }
            require(before == after, "Finite fluid created or lost volume");
        }
        for (int rotation = 0; rotation < 4; rotation++) {
            int[] result = thaumicadditions.machine.FiniteFluidFlow.balance(8, new int[]{0, 0, 0, 0}, rotation);
            int total = java.util.Arrays.stream(result).sum();
            require(total == 8 && java.util.Arrays.stream(result).max().orElseThrow() <= 2,
                "One bucket created new sources");
        }
    }

    private static void checkActivator() {
        for (int capacity : new int[]{0, 25, 50, 75, 100, 150, 2000, 4500, Integer.MAX_VALUE}) {
            int phase = 0;
            boolean rising = true;
            int activations = 0;
            int peaks = 0;
            for (int tick = 0; tick < 1000; tick++) {
                var next = thaumicadditions.machine.ActivatorMotion.advance(phase, rising, capacity);
                require(next.phase() >= 0 && next.phase() <= 100, "Activator phase escaped bounds");
                require(!next.activate() || phase == 0, "Activator triggered above lower endpoint");
                activations += next.activate() ? 1 : 0;
                peaks += next.peak() ? 1 : 0;
                require(activations - peaks >= 0 && activations - peaks <= 1, "Activator repeated an endpoint");
                require(next.phase() != phase, "Activator stopped oscillating");
                phase = next.phase();
                rising = next.rising();
            }
            require(activations > 1 && peaks > 1, "Activator did not complete cycles");
        }
    }

    private static void checkAccelerationGuard() {
        int[] calls = {0};
        require(thaumicadditions.machine.TickAcceleration.run(() -> {
            calls[0]++;
            require(!thaumicadditions.machine.TickAcceleration.run(() -> calls[0]++), "Recursive acceleration entered");
        }), "Outer acceleration was rejected");
        require(calls[0] == 1, "Acceleration recursion ran the target twice");
        try {
            thaumicadditions.machine.TickAcceleration.run(() -> {
                throw new IllegalStateException("Expected verification failure");
            });
            throw new AssertionError("Acceleration swallowed a ticker failure");
        } catch (IllegalStateException expected) {
            require(thaumicadditions.machine.TickAcceleration.run(() -> calls[0]++), "Guard remained locked after failure");
        }
        require(calls[0] == 2, "Guard failed to recover");
    }

    private static void checkTransfers() {
        Random random = new Random(37282351L);
        for (int attempt = 0; attempt < 30000; attempt++) {
            Reservoir source = new Reservoir(1 + random.nextInt(2005));
            Reservoir target = new Reservoir(1 + random.nextInt(2005));
            source.vis = random.nextInt(source.capacity + 1);
            source.taint = random.nextInt(source.capacity - source.vis + 1);
            target.vis = random.nextInt(target.capacity + 1);
            target.taint = random.nextInt(target.capacity - target.vis + 1);
            Direction side = Direction.values()[random.nextInt(6)];
            MagicKind kind = MagicKind.values()[random.nextInt(2)];
            int request = attempt % 97 == 0 ? Integer.MAX_VALUE : random.nextInt(4000) - 100;
            int initial = source.total() + target.total();
            int sourceBefore = source.stored(kind);
            int targetBefore = target.stored(kind);
            int expected = Math.min(Math.max(0, request), Math.min(sourceBefore, target.capacity - target.total()));
            int moved = MagicTransfer.move(source, target, kind, request, side);
            require(moved == expected, "Unexpected transfer amount");
            require(source.total() + target.total() == initial, "Transfer created or lost magic");
            require(source.stored(kind) == sourceBefore - moved, "Incorrect source debit");
            require(target.stored(kind) == targetBefore + moved, "Incorrect destination credit");
            source.checkBounds();
            target.checkBounds();
            int unchanged = source.total();
            require(MagicTransfer.move(source, source, kind, 10, side) == 0, "Self-transfer should be rejected");
            require(source.total() == unchanged, "Self-transfer changed contents");
            target.enabled = false;
            require(MagicTransfer.move(source, target, kind, 10, side) == 0, "Disabled side accepted magic");
        }
    }

    private static void checkDistribution() {
        Random random = new Random(9371882L);
        for (int attempt = 0; attempt < 20000; attempt++) {
            int count = 1 + random.nextInt(100);
            int vis = random.nextInt(count * 16 + 1);
            int taint = random.nextInt(count * 16 - vis + 1);
            List<MagicDistribution.Allocation> allocations = MagicDistribution.balance(vis, taint, count, 16);
            int allocatedVis = 0;
            int allocatedTaint = 0;
            int smallest = Integer.MAX_VALUE;
            int largest = 0;
            for (MagicDistribution.Allocation cell : allocations) {
                require(cell.vis() >= 0 && cell.taint() >= 0 && cell.vis() + cell.taint() <= 16, "Invalid pipe allocation");
                allocatedVis += cell.vis();
                allocatedTaint += cell.taint();
                smallest = Math.min(smallest, cell.vis() + cell.taint());
                largest = Math.max(largest, cell.vis() + cell.taint());
            }
            require(allocatedVis == vis && allocatedTaint == taint, "Network allocation created or lost magic");
            require(largest - smallest <= 1, "Unbalanced pipe allocation");
            int splitAt = count / 2;
            int firstComponent = allocations.subList(0, splitAt).stream().mapToInt(cell -> cell.vis() + cell.taint()).sum();
            int secondComponent = allocations.subList(splitAt, count).stream().mapToInt(cell -> cell.vis() + cell.taint()).sum();
            require(firstComponent + secondComponent == vis + taint, "Splitting storage lost magic");
        }
        boolean rejected = false;
        try {
            MagicDistribution.balance(17, 0, 1, 16);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        require(rejected, "Overfull physical storage was accepted");
    }

    private static void require(boolean value, String message) {
        if (!value) {
            throw new AssertionError(message);
        }
    }

    private static final class Reservoir implements MagicPort {
        private final int capacity;
        private int vis;
        private int taint;
        private boolean enabled = true;

        private Reservoir(int capacity) {
            this.capacity = capacity;
        }

        private int total() {
            return vis + taint;
        }

        private void checkBounds() {
            require(vis >= 0 && taint >= 0 && total() <= capacity, "Reservoir exceeded its bounds");
        }

        @Override
        public boolean canConnect(Direction side) {
            return enabled;
        }

        @Override
        public int stored(MagicKind kind) {
            return kind == MagicKind.VIS ? vis : taint;
        }

        @Override
        public int insert(MagicKind kind, int amount, Direction side, boolean simulate) {
            int accepted = enabled ? Math.min(Math.max(0, amount), capacity - total()) : 0;
            if (!simulate) {
                if (kind == MagicKind.VIS) {
                    vis += accepted;
                } else {
                    taint += accepted;
                }
            }
            return accepted;
        }

        @Override
        public int extract(MagicKind kind, int amount, Direction side, boolean simulate) {
            int extracted = enabled ? Math.min(Math.max(0, amount), stored(kind)) : 0;
            if (!simulate) {
                if (kind == MagicKind.VIS) {
                    vis -= extracted;
                } else {
                    taint -= extracted;
                }
            }
            return extracted;
        }
    }
}
