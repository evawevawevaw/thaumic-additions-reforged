package thaumicadditions.fx;

import java.util.function.Consumer;
import net.minecraft.world.level.Level;

public final class VisDrips {
    private static Consumer<Drip> emitter = drip -> {};

    private VisDrips() {
    }

    public static void setEmitter(Consumer<Drip> emitter) {
        VisDrips.emitter = java.util.Objects.requireNonNull(emitter);
    }

    public static void spawn(Level level, double x, double y, double z, int color, float gravity) {
        if (level.isClientSide) {
            emitter.accept(new Drip(level, x, y, z, color, gravity));
        }
    }

    public record Drip(Level level, double x, double y, double z, int color, float gravity) {
    }
}
