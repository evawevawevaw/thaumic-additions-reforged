package thaumicadditions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.menu.HyperNodeCreatorMenu;

public final class HyperNodeCreatorScreen extends AbstractContainerScreen<HyperNodeCreatorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/gui/guihypernodecreator.png");

    public HyperNodeCreatorScreen(HyperNodeCreatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 184;
        inventoryLabelY = 91;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (isHovering(0, 0, 176, 14, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.translatable(menu.primalsOnly() ? "gui.thaumicadditions.node_primals" :
                "gui.thaumicadditions.node_all", menu.totalItems()), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        GuiBlit.blit(graphics, TEXTURE, leftPos, topPos, 0, 0, 176, 70, 256, 256);
        GuiBlit.blit(graphics, TEXTURE, leftPos, topPos + 70, 0, 52, 176, 18, 256, 256);
        GuiBlit.blit(graphics, TEXTURE, leftPos, topPos + 88, 0, 70, 176, 96, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 3, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }
}
