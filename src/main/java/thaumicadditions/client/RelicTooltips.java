package thaumicadditions.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.item.RelicOwner;
import thaumicadditions.registry.ModDataComponents;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class RelicTooltips {
    private RelicTooltips() {
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        RelicOwner owner = event.getItemStack().get(ModDataComponents.RELIC_OWNER.get());
        if (owner != null && event.getEntity() != null && !owner.id().equals(event.getEntity().getUUID())) {
            event.getToolTip().add(Component.translatable("tooltip.thaumicadditions.relic_wrong_owner", owner.name()).withStyle(ChatFormatting.DARK_RED));
        }
    }
}
