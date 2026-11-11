package thaumicadditions.machine;

public final class FiniteFluidFlow {
    private FiniteFluidFlow() {
    }

    public static int downward(int source, int target) {
        if (source < 0 || source > 8 || target < 0 || target > 8) {
            throw new IllegalArgumentException("Invalid finite fluid quantity");
        }
        return Math.min(source, 8 - target);
    }

    public static int[] balance(int source, int[] neighbours, int first) {
        if (source < 0 || source > 8 || neighbours.length != 4) {
            throw new IllegalArgumentException("Invalid finite fluid cells");
        }
        int[] result = {source, neighbours[0], neighbours[1], neighbours[2], neighbours[3]};
        int total = source;
        int cells = 1;
        for (int value : neighbours) {
            if (value < -1 || value > 8) {
                throw new IllegalArgumentException("Invalid finite fluid neighbour");
            }
            if (value >= 0 && value < source - 1) {
                total += value;
                cells++;
            }
        }
        int each = total / cells;
        int extra = total % cells;
        result[0] = each + (extra > 0 ? 1 : 0);
        extra = Math.max(0, extra - 1);
        for (int offset = 0; offset < 4; offset++) {
            int index = Math.floorMod(first + offset, 4);
            if (neighbours[index] >= 0 && neighbours[index] < source - 1) {
                result[index + 1] = each + (extra > 0 ? 1 : 0);
                extra = Math.max(0, extra - 1);
            }
        }
        return result;
    }
}
