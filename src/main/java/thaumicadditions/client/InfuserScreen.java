package thaumicadditions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.menu.InfuserMenu;

public final class InfuserScreen extends AbstractContainerScreen<InfuserMenu> {
    private final ResourceLocation texture;

    public InfuserScreen(InfuserMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 240;
        inventoryLabelX = 8;
        inventoryLabelY = 149;
        titleLabelY = menu.dark() ? -2 : 1;
        texture = ResourceLocation.parse("thaumicadditions:textures/gui/" + (menu.dark() ? "guidarkinfuser" : "guiinfuser") + ".png");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        GuiBlit.blit(graphics, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        int height = menu.dark() ? 48 : 47;
        int vis = menu.progressPixels(false, height);
        int width = menu.dark() ? 6 : 9;
        int x = menu.dark() ? 158 : 160;
        if (vis > 0) {
            GuiBlit.blit(graphics, texture, leftPos + x, topPos + 105 + height - vis, 176, height - vis, width, vis, 256, 256);
        }
        if (menu.dark()) {
            int taint = menu.progressPixels(true, height);
            if (taint > 0) {
                GuiBlit.blit(graphics, texture, leftPos + 164, topPos + 105 + height - taint, 176, height - taint, 6, taint, 256, 256);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth - font.width(title)) / 2, titleLabelY, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }
}
