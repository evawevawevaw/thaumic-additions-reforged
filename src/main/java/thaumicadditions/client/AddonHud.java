package thaumicadditions.client;

import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import thaumcraft.client.platform.GuiBlit;
import thaumcraft.wand.WandItem;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.item.AdaminiteArmorItem;
import thaumicadditions.registry.ModDataComponents;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class AddonHud {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/gui/hud.png");
    private static Tip tip;

    private AddonHud() {
    }

    @SubscribeEvent
    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.parse("thaumicadditions:equipment_hud"), AddonHud::render);
    }

    public static void showMessage(Component text, int duration, int fadeIn, int fadeOut) {
        tip = new Tip(text.copy(), Util.getMillis(), Math.max(0L, duration) * 50,
            Math.max(0L, fadeIn) * 50, Math.max(0L, fadeOut) * 50);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }
        int charge = 0;
        int capacity = 0;
        for (var stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof AdaminiteArmorItem) {
                capacity += AdaminiteArmorItem.MAX_CHARGE;
                charge += Math.clamp(stack.getOrDefault(ModDataComponents.ARMOR_VIS_CHARGE.get(), 0), 0, AdaminiteArmorItem.MAX_CHARGE);
            }
        }
        if (capacity > 0) {
            int y = player.getMainHandItem().getItem() instanceof WandItem ? 50 : 0;
            int pixels = charge * 33 / capacity;
            GuiBlit.blit(graphics, TEXTURE, 4, y, 0, 0, 8, 33, 256, 256);
            if (pixels > 0) {
                int color = 0xFF000000 | Mth.hsvToRgb((Util.getMillis() % 10000) / 10000.0F, 1, 1);
                GuiBlit.blit(graphics, TEXTURE, 4, y + 33 - pixels, 8, 33 - pixels, 8, pixels, 256, 256, color);
            }
        }
        if (player.isUsingItem()) {
            var stack = player.getUseItem();
            int total = stack.getUseDuration(player);
            if (total > 0) {
                int x = (graphics.guiWidth() - 65) / 2;
                int pixels = (int)Math.clamp(player.getUseItemRemainingTicks() * 63L / total, 0, 63);
                GuiBlit.blit(graphics, TEXTURE, x, 0, 0, 33, 65, 17, 256, 256);
                if (pixels > 0) {
                    GuiBlit.blit(graphics, TEXTURE, x + 1, 0, 1, 50, pixels, 17, 256, 256);
                }
            }
        }
        renderTip(graphics, minecraft);
    }

    private static void renderTip(GuiGraphics graphics, Minecraft minecraft) {
        if (tip == null) {
            return;
        }
        long age = Util.getMillis() - tip.started;
        long ending = tip.fadeIn + tip.duration;
        if (age >= ending + tip.fadeOut) {
            tip = null;
            return;
        }
        float alpha = age < tip.fadeIn ? age / (float)Math.max(1L, tip.fadeIn)
            : age > ending ? (ending + tip.fadeOut - age) / (float)Math.max(1L, tip.fadeOut) : 1;
        int opacity = Math.clamp((int)(alpha * 255), 0, 255);
        if (opacity >= 4) {
            int y = graphics.guiHeight() / 2 + 4;
            var lines = minecraft.font.split(tip.text, Math.max(1, graphics.guiWidth() - 20));
            for (var line : lines) {
                graphics.drawString(minecraft.font, line, (graphics.guiWidth() - minecraft.font.width(line)) / 2,
                    y, opacity << 24 | 0xFFFFFF, false);
                y += 10;
            }
        }
    }

    private record Tip(Component text, long started, long duration, long fadeIn, long fadeOut) {
    }
}
