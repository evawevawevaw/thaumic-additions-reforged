package thaumicadditions.fx;

import java.util.function.Consumer;
import net.minecraft.world.level.Level;

public final class ColoredFlames {
    private static Consumer<Flame> emitter = flame -> {};

    private ColoredFlames() {
    }

    public static void setEmitter(Consumer<Flame> emitter) {
        ColoredFlames.emitter = java.util.Objects.requireNonNull(emitter);
    }

    public static void spawn(Level level, double x, double y, double z, int color, int lifetime, float scale) {
        if (level.isClientSide && lifetime > 0 && scale > 0) {
            emitter.accept(new Flame(level, x, y, z, color, lifetime, scale));
        }
    }

    public record Flame(Level level, double x, double y, double z, int color, int lifetime, float scale) {
    }
}
