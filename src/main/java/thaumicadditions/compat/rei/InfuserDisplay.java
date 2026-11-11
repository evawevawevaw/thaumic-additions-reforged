package thaumicadditions.compat.rei;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.crafting.InfuserRecipe;
import thaumicadditions.crafting.InfuserRecipes;
import thaumicadditions.registry.ModItems;

public final class InfuserDisplay extends BasicDisplay {
    public static final CategoryIdentifier<InfuserDisplay> REGULAR = CategoryIdentifier.of(ResourceLocation.parse("thaumicadditions:infuser"));
    public static final CategoryIdentifier<InfuserDisplay> DARK = CategoryIdentifier.of(ResourceLocation.parse("thaumicadditions:dark_infuser"));
    public static final DisplaySerializer<InfuserDisplay> SERIALIZER = new DisplaySerializer<>() {
        @Override
        public CompoundTag save(CompoundTag tag, InfuserDisplay display) {
            tag.putString("recipe", display.recipe.key());
            return tag;
        }

        @Override
        public InfuserDisplay read(CompoundTag tag) {
            InfuserRecipe recipe = InfuserRecipes.byKey(tag.getString("recipe"));
            if (recipe == null) {
                throw new IllegalArgumentException("Unknown infuser recipe: " + tag.getString("recipe"));
            }
            return new InfuserDisplay(recipe);
        }
    };
    private final InfuserRecipe recipe;

    public InfuserDisplay(InfuserRecipe recipe) {
        super(recipe.inputs().stream().map(input -> EntryIngredients.ofItemStacks(input.displayStacks())).toList(), outputs(recipe));
        this.recipe = recipe;
    }

    private static List<EntryIngredient> outputs(InfuserRecipe recipe) {
        List<EntryIngredient> outputs = new ArrayList<>();
        outputs.add(EntryIngredients.of(recipe.createResult()));
        if (recipe.depletedShard()) {
            outputs.add(EntryIngredients.of(new ItemStack(ModItems.DEPLETED_SHARD.get())));
        }
        return outputs;
    }

    public InfuserRecipe recipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<InfuserDisplay> getCategoryIdentifier() {
        return recipe.dark() ? DARK : REGULAR;
    }
}
