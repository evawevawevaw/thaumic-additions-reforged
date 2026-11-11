package thaumicadditions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.crafting.SealDyeRecipe;

public final class ModRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ThaumicAdditions.MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<SealDyeRecipe>> SEAL_DYE =
        SERIALIZERS.register("seal_dye", () -> new SimpleCraftingRecipeSerializer<>(SealDyeRecipe::new));

    private ModRecipeSerializers() {
    }

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
