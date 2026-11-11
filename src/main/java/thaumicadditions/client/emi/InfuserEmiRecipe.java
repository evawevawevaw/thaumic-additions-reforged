package thaumicadditions.client.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumicadditions.client.recipe.InfuserRecipeLayout;
import thaumicadditions.crafting.InfuserRecipe;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.registry.ModItems;

final class InfuserEmiRecipe implements EmiRecipe {
    private final EmiRecipeCategory category;
    private final ResourceLocation id;
    private final boolean dark;
    private final List<EmiIngredient> physicalInputs;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    InfuserEmiRecipe(EmiRecipeCategory category, InfuserRecipe source) {
        this.category = category;
        ResourceLocation sourceId = ResourceLocation.parse(source.key());
        id = ResourceLocation.fromNamespaceAndPath("thaumicadditions", "emi/" + (source.dark() ? "dark_infuser/" : "infuser/")
            + sourceId.getNamespace() + "/" + sourceId.getPath());
        dark = source.dark();
        physicalInputs = source.inputs().stream().map(input -> EmiIngredient.of(input.displayStacks().stream()
            .map(stack -> EmiStack.of(stack.copy())).toList()).copy()).toList();
        List<EmiIngredient> combined = new ArrayList<>(physicalInputs);
        if (source.visCost() > 0) {
            combined.add(new NetworkResourceStack(MagicKind.VIS, source.visCost()));
        }
        if (source.taintCost() > 0) {
            combined.add(new NetworkResourceStack(MagicKind.TAINT, source.taintCost()));
        }
        inputs = combined.stream().map(EmiIngredient::copy).toList();
        List<EmiStack> results = new ArrayList<>();
        results.add(EmiStack.of(source.createResult().copy()));
        if (source.depletedShard()) {
            results.add(EmiStack.of(ModItems.DEPLETED_SHARD.get()).setChance(0.5F));
        }
        outputs = results.stream().map(EmiStack::copy).toList();
    }

    boolean dark() {
        return dark;
    }

    List<EmiIngredient> physicalInputs() {
        return physicalInputs;
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
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return InfuserRecipeLayout.WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return InfuserRecipeLayout.HEIGHT;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int index = 0; index < physicalInputs.size(); index++) {
            var slot = widgets.addSlot(physicalInputs.get(index), InfuserRecipeLayout.x(dark, index), InfuserRecipeLayout.y(dark, index));
            if (physicalInputs.get(index).isEmpty()) {
                slot.appendTooltip(Component.translatable("recipe.thaumicadditions.empty_input"));
            }
        }
        widgets.addSlot(outputs.getFirst(), InfuserRecipeLayout.OUTPUT_X, InfuserRecipeLayout.OUTPUT_Y).recipeContext(this);
        if (outputs.size() > 1) {
            widgets.addSlot(outputs.get(1), InfuserRecipeLayout.OUTPUT_X, InfuserRecipeLayout.BONUS_Y).recipeContext(this)
                .appendTooltip(Component.translatable("screen.thaumicadditions.shard_chance"));
        }
        for (int index = physicalInputs.size(); index < inputs.size(); index++) {
            widgets.addSlot(inputs.get(index), 4 + 24 * (index - physicalInputs.size()), 142);
        }
        widgets.addTooltipText(List.of(Component.translatable("emi.thaumicadditions.network_supply")), 0, 140, 60, 24);
    }
}
