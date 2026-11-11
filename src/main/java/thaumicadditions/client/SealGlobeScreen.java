package thaumicadditions.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.blockentity.SealBlockEntity;
import thaumicadditions.menu.SealGlobeMenu;
import thaumicadditions.seal.SealBehaviors;

public final class SealGlobeScreen extends AbstractContainerScreen<SealGlobeMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/gui/guicrystalball.png");

    public SealGlobeScreen(SealGlobeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 235;
        imageHeight = 235;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        GuiBlit.blit(graphics, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (minecraft.level == null || !(minecraft.level.getBlockEntity(menu.pos()) instanceof SealBlockEntity seal)) {
            return;
        }
        List<FormattedCharSequence> lines = new ArrayList<>();
        for (String key : SealBehaviors.find(seal.data()).description()) {
            lines.addAll(font.split(Component.translatable(key), 185));
        }
        int y = (imageHeight - lines.size() * 10) / 2;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, (imageWidth - font.width(line)) / 2, y, 0xFF301D45, false);
            y += 10;
        }
    }
}
