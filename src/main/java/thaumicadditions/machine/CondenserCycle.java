package thaumicadditions.machine;

public final class CondenserCycle {
    public static final int LIFETIME = 24000;

    private CondenserCycle() {
    }

    public static Step advance(int remaining, int efficiency, boolean outputReady, boolean bufferReady) {
        if (remaining < 0) {
            return new Step(-1, false, false);
        }
        int next = Math.min(LIFETIME, remaining) - 1;
        if (next < 0) {
            return outputReady ? new Step(-1, false, true) : new Step(0, false, false);
        }
        int interval = 1000 / Math.max(1, Math.clamp(efficiency, 0, 2));
        boolean generated = next % interval == 0;
        return generated && !bufferReady ? new Step(remaining, false, false) : new Step(next, generated, false);
    }

    public record Step(int remaining, boolean generated, boolean expired) {
    }
}
