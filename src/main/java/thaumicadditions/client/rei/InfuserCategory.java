package thaumicadditions.client.rei;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import thaumicadditions.client.recipe.InfuserRecipeLayout;
import thaumicadditions.compat.rei.InfuserDisplay;
import thaumicadditions.registry.ModItems;

final class InfuserCategory implements DisplayCategory<InfuserDisplay> {
    private final boolean dark;

    InfuserCategory(boolean dark) {
        this.dark = dark;
    }

    @Override
    public CategoryIdentifier<InfuserDisplay> getCategoryIdentifier() {
        return dark ? InfuserDisplay.DARK : InfuserDisplay.REGULAR;
    }

    @Override
    public Component getTitle() {
        return InfuserRecipeLayout.title(dark);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(dark ? ModItems.DARK_INFUSER.get() : ModItems.INFUSER.get());
    }

    @Override
    public int getDisplayWidth(InfuserDisplay display) {
        return InfuserRecipeLayout.WIDTH + 10;
    }

    @Override
    public int getDisplayHeight() {
        return InfuserRecipeLayout.HEIGHT + 10;
    }

    @Override
    public List<Widget> setupDisplay(InfuserDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        int left = bounds.x + 5;
        int top = bounds.y + 5;
        for (int index = 0; index < display.getInputEntries().size(); index++) {
            int x = left + InfuserRecipeLayout.x(dark, index);
            int y = top + InfuserRecipeLayout.y(dark, index);
            var input = display.getInputEntries().get(index);
            widgets.add(Widgets.createSlot(new Point(x, y)).entries(input).markInput());
            if (input.isEmpty()) {
                widgets.add(Widgets.createTooltip(new Rectangle(x, y, 16, 16), Component.translatable("recipe.thaumicadditions.empty_input")));
            }
        }
        widgets.add(Widgets.createSlot(new Point(left + InfuserRecipeLayout.OUTPUT_X, top + InfuserRecipeLayout.OUTPUT_Y))
            .entries(display.getOutputEntries().getFirst()).markOutput());
        if (display.recipe().depletedShard()) {
            int x = left + InfuserRecipeLayout.OUTPUT_X;
            int y = top + InfuserRecipeLayout.BONUS_Y;
            widgets.add(Widgets.createSlot(new Point(x, y)).entries(display.getOutputEntries().get(1)).markOutput());
            widgets.add(Widgets.createTooltip(new Rectangle(x, y, 16, 16), Component.translatable("screen.thaumicadditions.shard_chance")));
        }
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, partialTick) ->
            InfuserRecipeLayout.draw(graphics, display.recipe(), left, top, 0xFF808080)));
        return widgets;
    }
}
