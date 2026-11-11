package thaumicadditions.client.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import thaumicadditions.client.recipe.InfuserRecipeLayout;
import thaumicadditions.crafting.InfuserRecipes;
import thaumicadditions.magic.MagicKind;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModMenus;

@EmiEntrypoint
public final class ThaumicAdditionsEmiPlugin implements EmiPlugin {
    @Override
    public void initialize(EmiInitRegistry registry) {
        registry.addIngredientSerializer(NetworkResourceStack.class, new NetworkResourceStack.Serializer());
    }

    @Override
    public void register(EmiRegistry registry) {
        EmiRecipeCategory regular = category("infuser", InfuserRecipeLayout.title(false), ModItems.INFUSER.get());
        EmiRecipeCategory dark = category("dark_infuser", InfuserRecipeLayout.title(true), ModItems.DARK_INFUSER.get());
        EmiRecipeCategory acquisition = category("acquisition", Component.translatable("emi.thaumicadditions.acquisition"), ModItems.VOID_CHEST.get());
        registry.addCategory(regular);
        registry.addCategory(dark);
        registry.addCategory(acquisition);
        registry.addWorkstation(regular, EmiStack.of(ModItems.INFUSER.get()));
        registry.addWorkstation(dark, EmiStack.of(ModItems.DARK_INFUSER.get()));
        for (MagicKind kind : MagicKind.values()) {
            var resource = new NetworkResourceStack(kind, 1);
            registry.addEmiStack(resource);
            registry.addAlias(resource, Component.translatable(kind == MagicKind.VIS
                ? "emi.thaumicadditions.alias_vis" : "emi.thaumicadditions.alias_taint"));
        }
        registry.addDeferredRecipes(add -> {
            InfuserRecipes.available(false).forEach(source -> add.accept(new InfuserEmiRecipe(regular, source)));
            InfuserRecipes.available(true).forEach(source -> add.accept(new InfuserEmiRecipe(dark, source)));
            add.accept(new AcquisitionEmiRecipe(acquisition, id("emi/acquisition/hyper_node_creator"), new ItemStack(ModItems.HYPER_NODE_CREATOR.get())));
            add.accept(new AcquisitionEmiRecipe(acquisition, id("emi/acquisition/void_chest"), new ItemStack(ModItems.VOID_CHEST.get())));
        });
        registry.addRecipeHandler(ModMenus.INFUSER.get(), new InfuserEmiTransfer());
    }

    private static EmiRecipeCategory category(String path, Component title, ItemLike icon) {
        return new EmiRecipeCategory(id(path), EmiStack.of(icon)) {
            @Override
            public Component getName() {
                return title;
            }
        };
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("thaumicadditions", path);
    }
}
