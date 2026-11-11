package thaumicadditions.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import thaumicadditions.registry.ModMenus;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.registry.ModBlockEntities;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import thaumicadditions.registry.ModItems;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class ClientRenderers {
    private ClientRenderers() {
    }

    @SubscribeEvent
    public static void registerItemModels(ModelEvent.RegisterAdditional event) {
        event.register(MachineItemRenderer.CRYSTALLIZER);
        event.register(MachineItemRenderer.GENERATOR);
    }

    @SubscribeEvent
    public static void registerItemRenderers(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private MachineItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new MachineItemRenderer();
                }
                return renderer;
            }
        }, ModItems.THAUMIC_CRYSTALLIZER.get(), ModItems.THAUMIC_GENERATOR.get(), ModItems.CREATIVE_NODE.get());
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MagicTankRenderer.LAYER, MagicTankRenderer::createLayer);
        event.registerLayerDefinition(CarpetRenderer.LAYER, CarpetRenderer::createLayer);
        event.registerLayerDefinition(HyperNodeCreatorRenderer.LAYER, HyperNodeCreatorRenderer::createLayer);
        event.registerLayerDefinition(ThaumicGeneratorRenderer.LAYER, ThaumicGeneratorRenderer::createLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(thaumicadditions.registry.ModEntities.CRUELTY_PROBE.get(),
            net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(thaumicadditions.registry.ModEntities.FOCUS_PROJECTILE.get(),
            thaumcraft.client.render.entity.PrimalOrbRenderer::new);
        event.registerEntityRenderer(thaumicadditions.registry.ModEntities.CARPET.get(), CarpetRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MAGIC_TANK.get(), MagicTankRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.INFUSER.get(), InfuserRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HYPER_NODE_CREATOR.get(), HyperNodeCreatorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.VOID_CHEST.get(), VoidChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SEAL.get(), SealRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CREATIVE_NODE.get(), CreativeNodeRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TILE_BOOSTER.get(), TileBoosterRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.INFUSION_ACTIVATOR.get(), InfusionActivatorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.THAUMIC_GENERATOR.get(), ThaumicGeneratorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRYSTALLIZER.get(), CrystallizerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CONDENSER.get(), CondenserRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.SEAL_GLOBE.get(), SealGlobeScreen::new);
        event.register(ModMenus.VOID_CHEST.get(), VoidChestScreen::new);
        event.register(ModMenus.INFUSER.get(), InfuserScreen::new);
        event.register(ModMenus.HYPER_NODE_CREATOR.get(), HyperNodeCreatorScreen::new);
        event.register(ModMenus.RESTORER.get(), RestorerScreen::new);
        event.register(ModMenus.DARK_GENERATOR.get(), DarkGeneratorScreen::new);
        event.register(ModMenus.THAUMIC_GENERATOR.get(), ThaumicGeneratorScreen::new);
        event.register(ModMenus.CRYSTALLIZER.get(), CrystallizerScreen::new);
        event.register(ModMenus.CONDENSER.get(), CondenserScreen::new);
        event.register(ModMenus.INFUSION_BOOK.get(), InfusionBookScreen::new);
    }

}
