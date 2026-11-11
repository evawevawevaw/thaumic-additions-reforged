package thaumicadditions.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.registry.ModItems;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class AcquisitionTooltips {
    private AcquisitionTooltips() {
    }

    public static Component description(ItemStack stack) {
        if (stack.is(ModItems.HYPER_NODE_CREATOR.get())) {
            return Component.translatable("tooltip.thaumicadditions.source.hyper_node_creator");
        }
        if (stack.is(ModItems.VOID_CHEST.get())) {
            return Component.translatable(Boolean.TRUE.equals(stack.get(ModDataComponents.VOID_CHEST_LOOT.get()))
                ? "tooltip.thaumicadditions.source.void_chest_loot" : "tooltip.thaumicadditions.source.void_chest");
        }
        if (stack.is(ModItems.SEAL_GLOBE.get()) || stack.is(ModItems.DARK_GENERATOR.get())
            || stack.is(ModItems.THAUMIC_GENERATOR.get()) || stack.is(ModItems.THAUMIC_CRYSTALLIZER.get())) {
            return Component.translatable("tooltip.thaumicadditions.source.infuser");
        }
        return Component.empty();
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        Component description = description(event.getItemStack());
        if (!description.getString().isEmpty()) {
            for (var line : Minecraft.getInstance().font.getSplitter().splitLines(description, 240, Style.EMPTY)) {
                event.getToolTip().add(Component.literal(line.getString()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
