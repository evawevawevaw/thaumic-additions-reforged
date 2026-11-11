package thaumicadditions.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.item.ArcaneDiceItem;
import thaumicadditions.registry.ModItems;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class DiceRendering {
    private DiceRendering() {
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(ModItems.ARCANE_DICE.get(), ResourceLocation.parse("thaumicadditions:rolling"),
            (stack, level, entity, seed) -> entity != null && entity.getUseItem() == stack ?
                ArcaneDiceItem.animationFrame(entity.getUseItemRemainingTicks()) / 3.0F : 0));
    }
}
