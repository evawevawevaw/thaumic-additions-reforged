package thaumicadditions.machine;

public final class TickAcceleration {
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    private TickAcceleration() {
    }

    public static boolean run(Runnable action) {
        if (ACTIVE.get()) {
            return false;
        }
        ACTIVE.set(true);
        try {
            action.run();
            return true;
        } finally {
            ACTIVE.remove();
        }
    }
}
