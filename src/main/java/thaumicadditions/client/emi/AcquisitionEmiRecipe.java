package thaumicadditions.client.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.client.AcquisitionTooltips;

record AcquisitionEmiRecipe(EmiRecipeCategory category, ResourceLocation id, ItemStack stack) implements EmiRecipe {
    AcquisitionEmiRecipe {
        stack = stack.copy();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(stack.copy()));
    }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 30 + Minecraft.getInstance().font.split(AcquisitionTooltips.description(stack), 152).size() * 10;
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(getOutputs().getFirst(), 71, 0).recipeContext(this);
        int y = 26;
        for (var line : Minecraft.getInstance().font.split(AcquisitionTooltips.description(stack), 152)) {
            widgets.addText(line, 4, y, 0xFF808080, false);
            y += 10;
        }
    }
}
