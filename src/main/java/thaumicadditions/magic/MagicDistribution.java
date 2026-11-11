package thaumicadditions.magic;

import java.util.ArrayList;
import java.util.List;

public final class MagicDistribution {
    public record Allocation(int vis, int taint) {
    }

    private MagicDistribution() {
    }

    public static List<Allocation> balance(int vis, int taint, int cells, int capacityPerCell) {
        if (vis < 0 || taint < 0 || cells <= 0 || capacityPerCell <= 0
            || (long) vis + taint > (long) cells * capacityPerCell) {
            throw new IllegalArgumentException("Invalid magic distribution");
        }
        long total = (long) vis + taint;
        int quotient = (int) (total / cells);
        int remainder = (int) (total % cells);
        List<Allocation> allocations = new ArrayList<>(cells);
        for (int i = 0; i < cells; i++) {
            int amount = quotient + (i < remainder ? 1 : 0);
            int localVis = Math.min(vis, amount);
            vis -= localVis;
            allocations.add(new Allocation(localVis, amount - localVis));
        }
        return List.copyOf(allocations);
    }
}
