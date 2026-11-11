package thaumicadditions.client.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import thaumicadditions.menu.InfuserMenu;

final class InfuserEmiTransfer implements StandardRecipeHandler<InfuserMenu> {
    @Override
    public List<Slot> getCraftingSlots(InfuserMenu menu) {
        return List.copyOf(menu.slots.subList(1, menu.dark() ? 6 : 7));
    }

    @Override
    public List<Slot> getInputSources(InfuserMenu menu) {
        List<Slot> sources = new ArrayList<>(getCraftingSlots(menu));
        int inventoryStart = menu.dark() ? 6 : 8;
        sources.addAll(menu.slots.subList(inventoryStart, inventoryStart + 36));
        return sources;
    }

    @Override
    public Slot getOutputSlot(InfuserMenu menu) {
        return menu.slots.getFirst();
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe instanceof InfuserEmiRecipe;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<InfuserMenu> context) {
        return recipe instanceof InfuserEmiRecipe infuser && infuser.dark() == context.getScreenHandler().dark()
            && StandardRecipeHandler.super.canCraft(new PhysicalRecipe(infuser), context);
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<InfuserMenu> context) {
        return recipe instanceof InfuserEmiRecipe infuser && infuser.dark() == context.getScreenHandler().dark()
            && StandardRecipeHandler.super.craft(new PhysicalRecipe(infuser), context);
    }

    private record PhysicalRecipe(InfuserEmiRecipe source) implements EmiRecipe {
        @Override
        public EmiRecipeCategory getCategory() {
            return source.getCategory();
        }

        @Override
        public ResourceLocation getId() {
            return source.getId();
        }

        @Override
        public List<EmiIngredient> getInputs() {
            return source.physicalInputs();
        }

        @Override
        public List<EmiStack> getOutputs() {
            return source.getOutputs();
        }

        @Override
        public int getDisplayWidth() {
            return source.getDisplayWidth();
        }

        @Override
        public int getDisplayHeight() {
            return source.getDisplayHeight();
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            source.addWidgets(widgets);
        }
    }
}
