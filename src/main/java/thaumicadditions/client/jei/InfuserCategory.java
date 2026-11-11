package thaumicadditions.client.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.client.recipe.InfuserRecipeLayout;
import thaumicadditions.crafting.InfuserRecipe;
import thaumicadditions.registry.ModItems;

final class InfuserCategory implements IRecipeCategory<InfuserRecipe> {
    static final RecipeType<InfuserRecipe> REGULAR = new RecipeType<>(ResourceLocation.parse("thaumicadditions:infuser"), InfuserRecipe.class);
    static final RecipeType<InfuserRecipe> DARK = new RecipeType<>(ResourceLocation.parse("thaumicadditions:dark_infuser"), InfuserRecipe.class);
    private final boolean dark;
    private final IDrawable icon;

    InfuserCategory(IGuiHelper helper, boolean dark) {
        this.dark = dark;
        icon = helper.createDrawableItemLike(dark ? ModItems.DARK_INFUSER.get() : ModItems.INFUSER.get());
    }

    @Override
    public RecipeType<InfuserRecipe> getRecipeType() {
        return dark ? DARK : REGULAR;
    }

    @Override
    public Component getTitle() {
        return InfuserRecipeLayout.title(dark);
    }

    @Override
    public int getWidth() {
        return InfuserRecipeLayout.WIDTH;
    }

    @Override
    public int getHeight() {
        return InfuserRecipeLayout.HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfuserRecipe recipe, IFocusGroup focuses) {
        for (int index = 0; index < recipe.inputs().size(); index++) {
            builder.addInputSlot(InfuserRecipeLayout.x(dark, index), InfuserRecipeLayout.y(dark, index))
                .setStandardSlotBackground().addItemStacks(recipe.inputs().get(index).displayStacks());
        }
        builder.addOutputSlot(InfuserRecipeLayout.OUTPUT_X, InfuserRecipeLayout.OUTPUT_Y)
            .setOutputSlotBackground().addItemStack(recipe.createResult());
        if (recipe.depletedShard()) {
            builder.addOutputSlot(InfuserRecipeLayout.OUTPUT_X, InfuserRecipeLayout.BONUS_Y)
                .setStandardSlotBackground().addItemStack(new ItemStack(ModItems.DEPLETED_SHARD.get()))
                .addRichTooltipCallback((slot, tooltip) -> tooltip.add(Component.translatable("screen.thaumicadditions.shard_chance")));
        }
    }

    @Override
    public void draw(InfuserRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        InfuserRecipeLayout.draw(graphics, recipe, 0, 0, 0xFF404040);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, InfuserRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
        for (int index = 0; index < recipe.inputs().size(); index++) {
            int x = InfuserRecipeLayout.x(dark, index);
            int y = InfuserRecipeLayout.y(dark, index);
            if (recipe.inputs().get(index).displayStacks().isEmpty() && mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                tooltip.add(Component.translatable("recipe.thaumicadditions.empty_input"));
            }
        }
    }
}
