package thaumicadditions.compat.rei;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.forge.REIPluginCommon;

@REIPluginCommon
public final class ThaumicAdditionsReiCommonPlugin implements REIServerPlugin {
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(InfuserDisplay.REGULAR, InfuserDisplay.SERIALIZER);
        registry.register(InfuserDisplay.DARK, InfuserDisplay.SERIALIZER);
    }
}
