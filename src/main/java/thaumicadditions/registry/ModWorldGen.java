package thaumicadditions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.world.MonolithFeature;

public final class ModWorldGen {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ThaumicAdditions.MOD_ID);

    private ModWorldGen() {
    }

    public static void register(IEventBus bus) {
        FEATURES.register("monolith", MonolithFeature::new);
        FEATURES.register(bus);
    }
}
