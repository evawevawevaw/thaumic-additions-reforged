package thaumicadditions.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.menu.MachineMenuLayout;

final class MachineScreenLayout {
    static final int HEADER = MachineMenuLayout.HEADER;

    private MachineScreenLayout() {
    }

    static void background(GuiGraphics graphics, ResourceLocation texture, int x, int y, int height, int color) {
        background(graphics, texture, x, y, height, height, color);
    }

    static void background(GuiGraphics graphics, ResourceLocation texture, int x, int y, int sourceHeight,
        int height, int color) {
        graphics.fill(x, y, x + 176, y + height + HEADER, 0xFF18151C);
        graphics.fill(x + 1, y + 1, x + 175, y + height + HEADER - 1, color);
        GuiBlit.blit(graphics, texture, x, y + HEADER, 176, height, 0, 0, 176, sourceHeight, 256, 256, -1);
    }
}
