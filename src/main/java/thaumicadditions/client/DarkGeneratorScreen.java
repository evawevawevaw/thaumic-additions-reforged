package thaumicadditions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.menu.DarkGeneratorMenu;
import thaumicadditions.menu.MachineMenuLayout;

public final class DarkGeneratorScreen extends AbstractContainerScreen<DarkGeneratorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/gui/guidarkgen.png");

    public DarkGeneratorScreen(DarkGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166 + MachineMenuLayout.HEADER + MachineMenuLayout.DARK_INVENTORY_GAP;
        inventoryLabelY = 78 + MachineMenuLayout.HEADER + 3;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (isHovering(56, 64 + MachineScreenLayout.HEADER, 64, 8, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.translatable(menu.hasObelisk() ? "gui.thaumicadditions.obelisk_found" :
                "gui.thaumicadditions.obelisk_required"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF18151C);
        graphics.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xFF352843);
        GuiBlit.blit(graphics, TEXTURE, leftPos, topPos + MachineMenuLayout.HEADER, 0, 0, 176, 78, 256, 256);
        GuiBlit.blit(graphics, TEXTURE, leftPos,
            topPos + MachineMenuLayout.HEADER + 78 + MachineMenuLayout.DARK_INVENTORY_GAP,
            0, 78, 176, 88, 256, 256);
        GuiBlit.blit(graphics, TEXTURE, leftPos + 84, topPos + MachineScreenLayout.HEADER + 44, 192, menu.moonPhase() * 8, 8, 8, 256, 256);
        GuiBlit.blit(graphics, TEXTURE, leftPos + 80, topPos + MachineScreenLayout.HEADER + 24, 176, menu.brightness() * 16, 16, 16, 256, 256);
        int width = menu.progressPixels(64);
        if (width > 0) {
            GuiBlit.blit(graphics, TEXTURE, leftPos + 56, topPos + MachineScreenLayout.HEADER + 64, 192, 64, width, 8, 256, 256);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 6, 0xFFE9DFF5, false);
        graphics.drawString(font, playerInventoryTitle, (imageWidth - font.width(playerInventoryTitle)) / 2,
            inventoryLabelY, 0xFFE9DFF5, false);
    }
}
