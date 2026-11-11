package thaumicadditions.machine;

public final class ActivatorMotion {
    private ActivatorMotion() {
    }

    public static Step advance(int phase, boolean rising, int capacity) {
        int current = Math.clamp(phase, 0, 100);
        boolean trigger = current == 0;
        boolean up = trigger || current < 100 && rising;
        int speed = Math.max(1, (int)(Math.sqrt(Math.max(0, capacity)) / 5));
        int next = (int)Math.clamp((long)current + (up ? speed : -speed), 0, 100);
        return new Step(next, up, trigger, current == 100);
    }

    public record Step(int phase, boolean rising, boolean activate, boolean peak) {
    }
}
