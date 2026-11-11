package thaumicadditions.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.crafting.InfuserRecipe;
import thaumicadditions.crafting.InfuserRecipes;
import thaumicadditions.menu.InfusionBookMenu;

public final class InfusionBookScreen extends AbstractContainerScreen<InfusionBookMenu> {
    private static final int[][] REGULAR = {{80, 11}, {50, 55}, {110, 55}, {28, 103}, {80, 106}, {132, 103}};
    private static final int[][] DARK = {{80, 16}, {26, 54}, {133, 54}, {49, 117}, {112, 119}};
    private final ResourceLocation texture;
    private List<InfuserRecipe> recipes = List.of();
    private int page;
    private ItemStack hovered = ItemStack.EMPTY;

    public InfusionBookScreen(InfusionBookMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 226;
        texture = ResourceLocation.parse("thaumicadditions:textures/gui/" + (menu.dark() ? "guidarkbook" : "guibook") + ".png");
    }

    @Override
    protected void init() {
        super.init();
        recipes = InfuserRecipes.available(menu.dark());
        page = Math.clamp(page, 0, Math.max(0, recipes.size() - 1));
        addRenderableWidget(Button.builder(Component.literal("<"), button -> page = Math.max(0, page - 1))
            .bounds(leftPos + 8, topPos + 204, 22, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), button -> page = Math.min(Math.max(0, recipes.size() - 1), page + 1))
            .bounds(leftPos + 146, topPos + 204, 22, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hovered = ItemStack.EMPTY;
        super.render(graphics, mouseX, mouseY, partialTick);
        if (!hovered.isEmpty()) {
            graphics.renderTooltip(font, hovered, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        GuiBlit.blit(graphics, texture, leftPos, topPos, 0, 0, 176, 200, 256, 256);
        if (recipes.isEmpty()) {
            return;
        }
        InfuserRecipe recipe = recipes.get(page);
        int[][] positions = menu.dark() ? DARK : REGULAR;
        for (int i = 0; i < recipe.inputs().size(); i++) {
            List<ItemStack> variants = recipe.inputs().get(i).displayStacks();
            if (!variants.isEmpty()) {
                int index = minecraft.player == null ? 0 : minecraft.player.tickCount / 20 % variants.size();
                drawStack(graphics, variants.get(index), positions[i][0], positions[i][1], mouseX, mouseY);
            }
        }
        drawStack(graphics, recipe.createResult(), 80, 72, mouseX, mouseY);
    }

    private void drawStack(GuiGraphics graphics, ItemStack stack, int x, int y, int mouseX, int mouseY) {
        graphics.renderItem(stack, leftPos + x, topPos + y);
        graphics.renderItemDecorations(font, stack, leftPos + x, topPos + y);
        if (mouseX >= leftPos + x && mouseX < leftPos + x + 16 && mouseY >= topPos + y && mouseY < topPos + y + 16) {
            hovered = stack;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (recipes.isEmpty()) {
            graphics.drawWordWrap(font, Component.translatable("screen.thaumicadditions.no_infuser_recipes"), 12, 150, 150, 0x302010);
            return;
        }
        InfuserRecipe recipe = recipes.get(page);
        graphics.drawWordWrap(font, recipe.createResult().getHoverName(), 12, 151, 150, 0x302010);
        graphics.drawString(font, Component.translatable("screen.thaumicadditions.infuser_cost", recipe.visCost(), recipe.taintCost()),
            12, 177, 0x302010, false);
        if (recipe.depletedShard()) {
            graphics.drawString(font, Component.translatable("screen.thaumicadditions.shard_chance"), 12, 187, 0x302010, false);
        }
        Component pages = Component.literal((page + 1) + " / " + recipes.size());
        graphics.drawString(font, pages, (imageWidth - font.width(pages)) / 2, 210, 0xFFFFFF, true);
    }
}
