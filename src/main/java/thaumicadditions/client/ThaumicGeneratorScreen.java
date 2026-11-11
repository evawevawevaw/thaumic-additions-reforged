package thaumicadditions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.machine.VisEnergyBuffer;
import thaumicadditions.menu.ThaumicGeneratorMenu;

public final class ThaumicGeneratorScreen extends AbstractContainerScreen<ThaumicGeneratorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/gui/guigenerator.png");

    public ThaumicGeneratorScreen(ThaumicGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 102;
        imageHeight = 82;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (isHovering(47, 22, 9, 47, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.translatable("gui.thaumicadditions.energy", menu.energy(), VisEnergyBuffer.CAPACITY), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        GuiBlit.blit(graphics, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        int height = Math.clamp(menu.energy() * 47 / VisEnergyBuffer.CAPACITY, 0, 47);
        if (height > 0) {
            GuiBlit.blit(graphics, TEXTURE, leftPos + 47, topPos + 69 - height, 102, 47 - height, 9, height, 256, 256);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawWordWrap(font, title, 4, 3, imageWidth - 8, 0x404040);
    }
}
