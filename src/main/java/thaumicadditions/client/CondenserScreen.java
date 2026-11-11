package thaumicadditions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.machine.UpgradeType;
import thaumicadditions.menu.CondenserMenu;

public final class CondenserScreen extends AbstractContainerScreen<CondenserMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/gui/guicondenser.png");

    public CondenserScreen(CondenserMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = 73;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (isHovering(78, 23, 20, 36, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.translatable("gui.thaumicadditions.buffered_vis", menu.bufferedVis()), mouseX, mouseY);
        }
        for (int slot = 0; slot < 2; slot++) {
            int type = menu.upgradeIndex(slot);
            if (type >= 0 && type < UpgradeType.values().length && isHovering(26 + slot * 108, 14, 16, 16, mouseX, mouseY)) {
                graphics.renderTooltip(font, Component.translatable("item.thaumicadditions.upgrade_" + UpgradeType.values()[type].id()), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        GuiBlit.blit(graphics, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        GuiBlit.blit(graphics, TEXTURE, leftPos + 78, topPos + 23, 196, 0, 20, 36, 256, 256);
        int height = menu.progressPixels(36);
        if (height > 0) {
            GuiBlit.blit(graphics, TEXTURE, leftPos + 78, topPos + 59 - height, 176, 36 - height, 20, height, 256, 256, menu.crystalColor());
        }
        for (int slot = 0; slot < 2; slot++) {
            int type = menu.upgradeIndex(slot);
            if (type >= 0 && type < UpgradeType.values().length) {
                ResourceLocation icon = ResourceLocation.parse("thaumicadditions:textures/item/upgrade_" + UpgradeType.values()[type].id() + ".png");
                GuiBlit.blit(graphics, icon, leftPos + 26 + slot * 108, topPos + 14, 0, 0, 16, 16, 16, 16);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 4, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }
}
