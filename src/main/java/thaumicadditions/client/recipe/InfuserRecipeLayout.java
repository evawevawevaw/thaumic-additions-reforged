package thaumicadditions.client.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import thaumicadditions.crafting.InfuserRecipe;

public final class InfuserRecipeLayout {
    public static final int WIDTH = 160;
    public static final int HEIGHT = 170;
    public static final int OUTPUT_X = 72;
    public static final int OUTPUT_Y = 58;
    public static final int BONUS_Y = 118;
    private static final int[][] REGULAR = {{72, 4}, {42, 40}, {102, 40}, {20, 88}, {72, 92}, {124, 88}};
    private static final int[][] DARK = {{72, 4}, {24, 40}, {120, 40}, {44, 92}, {100, 92}};

    private InfuserRecipeLayout() {
    }

    public static int x(boolean dark, int slot) {
        return (dark ? DARK : REGULAR)[slot][0];
    }

    public static int y(boolean dark, int slot) {
        return (dark ? DARK : REGULAR)[slot][1];
    }

    public static Component title(boolean dark) {
        return Component.translatable(dark ? "recipe.thaumicadditions.dark_infuser" : "recipe.thaumicadditions.infuser");
    }

    public static void draw(GuiGraphics graphics, InfuserRecipe recipe, int left, int top, int color) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, Component.translatable("screen.thaumicadditions.infuser_cost", recipe.visCost(), recipe.taintCost()),
            left + 4, top + 144, color, false);
        if (recipe.depletedShard()) {
            graphics.drawString(font, Component.translatable("screen.thaumicadditions.shard_chance"), left + 4, top + 156, color, false);
        }
    }
}
