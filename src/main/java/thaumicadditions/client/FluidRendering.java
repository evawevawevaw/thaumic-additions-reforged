package thaumicadditions.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.registry.ModFluids;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class FluidRendering {
    private FluidRendering() {
    }

    @SubscribeEvent
    public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(ModFluids.PURE_WARP.get(), net.minecraft.client.renderer.RenderType.translucent());
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_PURE_WARP.get(), net.minecraft.client.renderer.RenderType.translucent());
        });
    }

    @SubscribeEvent
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.parse("thaumicadditions:block/fluids/pure_warp_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.parse("thaumicadditions:block/fluids/pure_warp_flowing");
            }
        }, ModFluids.PURE_WARP_TYPE);
    }
}
