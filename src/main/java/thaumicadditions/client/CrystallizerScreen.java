package thaumicadditions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.menu.CrystallizerMenu;
import thaumicadditions.menu.MachineMenuLayout;

public final class CrystallizerScreen extends AbstractContainerScreen<CrystallizerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/gui/guicrystalizer.png");

    public CrystallizerScreen(CrystallizerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = MachineMenuLayout.CRYSTALLIZER_BODY + MachineMenuLayout.HEADER;
        inventoryLabelY = MachineMenuLayout.crystallizerY(147);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        MachineScreenLayout.background(graphics, TEXTURE, leftPos, topPos, 240,
            MachineMenuLayout.CRYSTALLIZER_BODY, 0xFFC6C6C6);
        int progress = menu.progressPixels(47);
        if (progress > 0) {
            meter(graphics, 160, 152, 176, 47, 9, progress);
        }
        int charge = menu.chargePixels(31);
        if (charge > 0) {
            meter(graphics, 161, 39, 192, 31, 7, charge);
        }
    }

    private void meter(GuiGraphics graphics, int x, int bottom, int u, int v, int width, int height) {
        int y = MachineMenuLayout.crystallizerY(bottom - height);
        int renderedHeight = MachineMenuLayout.crystallizerY(bottom) - y;
        GuiBlit.blit(graphics, TEXTURE, leftPos + x, topPos + y, width, renderedHeight,
            u, v - height, width, height, 256, 256, -1);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }
}
