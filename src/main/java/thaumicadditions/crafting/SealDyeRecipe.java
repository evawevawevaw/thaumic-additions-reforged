package thaumicadditions.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thaumicadditions.item.SealItem;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModRecipeSerializers;
import thaumicadditions.seal.SealData;

public final class SealDyeRecipe extends CustomRecipe {
    public SealDyeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return ingredients(input) != null;
    }

    private static Ingredients ingredients(CraftingInput input) {
        ItemStack seal = ItemStack.EMPTY;
        List<DyeItem> dyes = new ArrayList<>();
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(ModItems.SEAL_ITEM.get()) && seal.isEmpty()) {
                seal = stack;
            } else if (stack.getItem() instanceof DyeItem dye) {
                dyes.add(dye);
            } else {
                return null;
            }
        }
        return seal.isEmpty() || dyes.isEmpty() ? null : new Ingredients(seal, dyes);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        Ingredients ingredients = ingredients(input);
        if (ingredients == null) {
            return ItemStack.EMPTY;
        }
        SealData data = SealItem.data(ingredients.seal);
        ItemStack palette = new ItemStack(Items.LEATHER_HELMET);
        palette.set(DataComponents.DYED_COLOR, new DyedItemColor(data.color(), false));
        ItemStack mixed = DyedItemColor.applyDyes(palette, ingredients.dyes);
        int color = mixed.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(data.color(), false)).rgb();
        ItemStack result = ingredients.seal.copyWithCount(1);
        result.set(ModDataComponents.SEAL_DATA.get(), new SealData(color, data.aspects()));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SEAL_DYE.get();
    }

    private record Ingredients(ItemStack seal, List<DyeItem> dyes) {
    }
}
