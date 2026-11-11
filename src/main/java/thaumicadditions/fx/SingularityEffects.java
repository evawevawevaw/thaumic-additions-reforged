package thaumicadditions.fx;

import java.util.function.Consumer;
import thaumicadditions.entity.SingularityEntity;

public final class SingularityEffects {
    private static Consumer<SingularityEntity> emitter = entity -> {};

    private SingularityEffects() {
    }

    public static void setEmitter(Consumer<SingularityEntity> value) {
        emitter = java.util.Objects.requireNonNull(value);
    }

    public static void emit(SingularityEntity entity) {
        emitter.accept(entity);
    }
}
