package thaumicadditions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.menu.RestorerMenu;

public final class RestorerScreen extends AbstractContainerScreen<RestorerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/gui/guirepairer.png");

    public RestorerScreen(RestorerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166 + MachineScreenLayout.HEADER;
        inventoryLabelY = 73 + MachineScreenLayout.HEADER;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        MachineScreenLayout.background(graphics, TEXTURE, leftPos, topPos, 166, 0xFFC6C6C6);
        int height = menu.progressPixels(31);
        if (height > 0) {
            GuiBlit.blit(graphics, TEXTURE, leftPos + 135, topPos + MachineScreenLayout.HEADER + 47 - height, 176, 31 - height, 7, height, 256, 256);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }
}
