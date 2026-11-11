package thaumicadditions.crafting;

import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import thaumcraft.crafting.TcIngredient;
import thaumcraft.crafting.TcStack;

public record InfuserRecipe(String key, boolean dark, int visCost, int taintCost, TcStack result,
    List<TcIngredient> inputs, boolean depletedShard) {
    public InfuserRecipe {
        inputs = List.copyOf(inputs);
        if (inputs.size() != (dark ? 5 : 6) || visCost < 0 || visCost > 32767 || taintCost < 0
            || taintCost > 32767 || !dark && taintCost != 0 || dark && depletedShard) {
            throw new IllegalArgumentException("Invalid infuser recipe: " + key);
        }
    }

    public boolean matches(Container inventory) {
        if (!result.exists()) {
            return false;
        }
        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).test(inventory.getItem(i + 1))) {
                return false;
            }
        }
        return true;
    }

    public ItemStack createResult() {
        return result.create();
    }
}
