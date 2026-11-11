package thaumicadditions.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import thaumicadditions.client.AcquisitionTooltips;
import thaumicadditions.crafting.InfuserRecipes;
import thaumicadditions.registry.ModItems;

@JeiPlugin
public final class ThaumicAdditionsJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.parse("thaumicadditions:jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (ModList.get().isLoaded("emi")) {
            return;
        }
        var helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new InfuserCategory(helper, false), new InfuserCategory(helper, true));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (ModList.get().isLoaded("emi")) {
            return;
        }
        registration.addRecipes(InfuserCategory.REGULAR, InfuserRecipes.available(false));
        registration.addRecipes(InfuserCategory.DARK, InfuserRecipes.available(true));
        for (var item : new net.minecraft.world.item.Item[]{ModItems.HYPER_NODE_CREATOR.get(), ModItems.VOID_CHEST.get()}) {
            ItemStack stack = new ItemStack(item);
            registration.addItemStackInfo(stack, AcquisitionTooltips.description(stack));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (ModList.get().isLoaded("emi")) {
            return;
        }
        registration.addRecipeCatalysts(InfuserCategory.REGULAR, ModItems.INFUSER.get());
        registration.addRecipeCatalysts(InfuserCategory.DARK, ModItems.DARK_INFUSER.get());
    }
}
