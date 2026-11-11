package thaumicadditions.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue MAX_BOOST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        MAX_BOOST = builder.defineInRange("maxBoostAmount", 5, 1, 15);
        SPEC = builder.build();
    }

    private ModConfig() {
    }
}
