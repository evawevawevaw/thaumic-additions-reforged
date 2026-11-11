package thaumicadditions.client;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import thaumcraft.client.fx.WispFx;
import thaumcraft.client.platform.WorldFxRenderer;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.fx.SingularityEffects;
import thaumicadditions.registry.ModEntities;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class SingularityRendering {
    private SingularityRendering() {
    }

    @SubscribeEvent
    public static void renderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SINGULARITY.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> SingularityEffects.setEmitter(entity -> {
            for (int particle = 0; particle < 6; particle++) {
                WispFx effect = new WispFx(entity.level().random, entity.getX(), entity.getY() + entity.lift(), entity.getZ(),
                    entity.energy() / 500, 5, null).shrink(true);
                effect.gravity(0);
                WorldFxRenderer.add(effect);
            }
        }));
    }
}
