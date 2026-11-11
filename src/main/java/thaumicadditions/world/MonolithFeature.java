package thaumicadditions.world;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class MonolithFeature extends Feature<NoneFeatureConfiguration> {
    public MonolithFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level = context.level();
        var pos = context.origin().below();
        if (level.getLevel().dimension() != Level.OVERWORLD || pos.getY() <= 68) {
            return false;
        }
        return MonolithStructure.apply(level, MonolithStructure.plan(pos, false, true), null, true);
    }
}
