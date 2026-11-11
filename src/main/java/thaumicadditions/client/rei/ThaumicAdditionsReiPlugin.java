package thaumicadditions.client.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import thaumicadditions.compat.rei.InfuserDisplay;
import thaumicadditions.crafting.InfuserRecipes;
import thaumicadditions.registry.ModItems;

@REIPluginClient
public final class ThaumicAdditionsReiPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new InfuserCategory(false), new InfuserCategory(true));
        registry.addWorkstations(InfuserDisplay.REGULAR, EntryStacks.of(ModItems.INFUSER.get()));
        registry.addWorkstations(InfuserDisplay.DARK, EntryStacks.of(ModItems.DARK_INFUSER.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        InfuserRecipes.available(false).forEach(recipe -> registry.add(new InfuserDisplay(recipe)));
        InfuserRecipes.available(true).forEach(recipe -> registry.add(new InfuserDisplay(recipe)));
    }
}
