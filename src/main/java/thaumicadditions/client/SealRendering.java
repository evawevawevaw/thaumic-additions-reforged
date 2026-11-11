package thaumicadditions.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import thaumcraft.aspect.Aspect;
import thaumcraft.research.Knowledge;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.item.SealItem;
import thaumicadditions.item.SealSymbolItem;
import thaumicadditions.registry.ModItems;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class SealRendering {
    private SealRendering() {
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(ModItems.SEAL_SYMBOL.get())) {
            return;
        }
        Aspect aspect = SealSymbolItem.aspect(event.getItemStack());
        if (aspect != null) {
            event.getToolTip().add(event.getEntity() != null && Knowledge.get(event.getEntity()).hasDiscoveredAspect(aspect)
                ? Component.literal(aspect.getName()) : Component.literal("???"));
        }
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.SEAL_ITEM.get(), ResourceLocation.parse("thaumicadditions:symbols"),
                (stack, level, entity, seed) -> SealItem.data(stack).aspects().size());
            ItemProperties.register(ModItems.SEAL_SYMBOL.get(), ResourceLocation.parse("thaumicadditions:filled"),
                (stack, level, entity, seed) -> SealSymbolItem.aspect(stack) == null ? 0 : 1);
        });
    }

    @SubscribeEvent
    public static void colors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> {
            var data = SealItem.data(stack);
            if (layer == 1) {
                return 0xFF000000 | data.color();
            }
            if (layer >= 2 && layer - 2 < data.aspects().size()) {
                Aspect aspect = Aspect.getAspect(data.aspects().get(layer - 2));
                return aspect == null ? -1 : 0xFF000000 | aspect.getColor();
            }
            return -1;
        }, ModItems.SEAL_ITEM.get());
        event.register((stack, layer) -> {
            Aspect aspect = SealSymbolItem.aspect(stack);
            return layer == 1 && aspect != null ? 0xFF000000 | aspect.getColor() : -1;
        }, ModItems.SEAL_SYMBOL.get());
    }
}
