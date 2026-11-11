package thaumicadditions.crafting;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.Container;
import thaumcraft.crafting.TcIngredient;
import thaumcraft.crafting.TcStack;

public final class InfuserRecipes {
    private static final Map<String, InfuserRecipe> RECIPES = new LinkedHashMap<>();

    private InfuserRecipes() {
    }

    public static void register(InfuserRecipe recipe) {
        if (RECIPES.putIfAbsent(recipe.key(), recipe) != null) {
            throw new IllegalStateException("Duplicate infuser recipe: " + recipe.key());
        }
    }

    public static Collection<InfuserRecipe> all() {
        return Collections.unmodifiableCollection(RECIPES.values());
    }

    public static List<InfuserRecipe> available(boolean dark) {
        return RECIPES.values().stream().filter(recipe -> recipe.dark() == dark && recipe.result().exists()
            && recipe.inputs().stream().allMatch(input -> input.exists())).toList();
    }

    public static InfuserRecipe byKey(String key) {
        return RECIPES.get(key);
    }

    public static InfuserRecipe find(boolean dark, Container container) {
        for (InfuserRecipe recipe : RECIPES.values()) {
            if (recipe.dark() == dark && recipe.matches(container)) {
                return recipe;
            }
        }
        return null;
    }

    public static void init() {
        regular("seal_globe", 16, "thaumicadditions:seal_globe", false,
            "minecraft:gold_ingot", "minecraft:glass", "minecraft:glass", "minecraft:gold_ingot",
            "thaumicadditions:seal_symbol", "minecraft:gold_ingot");
        regular("dark_generator", 64, "thaumicadditions:dark_generator", true,
            "thaumcraft:void_seed", "thaumcraft:thaumium_ingot", "thaumcraft:thaumium_ingot",
            "thaumcraft:void_metal_ingot", "thaumcraft:balanced_shard", "thaumcraft:void_metal_ingot");
        regular("dark_infuser", 18, "thaumicadditions:dark_infuser", false,
            "thaumicadditions:infuser", "thaumcraft:thaumium_ingot", "thaumcraft:thaumium_ingot",
            "thaumcraft:thaumium_ingot", "thaumcraft:nitor", "thaumcraft:thaumium_ingot");
        regular("thaumic_crystallizer", 12, "thaumicadditions:thaumic_crystallizer", true,
            "thaumcraft:balanced_shard", "thaumcraft:thaumium_ingot", "minecraft:gold_ingot",
            "minecraft:gold_ingot", "thaumcraft:quicksilver_drop", "thaumcraft:thaumium_ingot");
        regular("thaumic_generator", 18, "thaumicadditions:thaumic_generator", false,
            "minecraft:glass", "thaumcraft:salis_mundus", "thaumcraft:salis_mundus", null, "thaumcraft:mirrored_glass", null);
        regular("infuser_book", 4, "thaumicadditions:infuser_book", false, "minecraft:book", null, null, null, null, null);
        dark("dark_infuser_book", 4, 2, "thaumicadditions:dark_infuser_book", "minecraft:book", null, null, null, null);
        dark("soul_fragment", 8, 4, "thaumicadditions:soul_fragment",
            "minecraft:soul_sand", "minecraft:soul_sand", "minecraft:soul_sand", "minecraft:soul_sand", "minecraft:soul_sand");
        dark("aura_node", 16, 6, "thaumcraft:aura_node", "thaumicadditions:soul_fragment",
            "thaumcraft:wisp_essence", "thaumcraft:wisp_essence", "minecraft:diamond", "minecraft:gold_ingot");
        dark("eldritch_keystone_tlhutlh", 16, 6, "thaumicadditions:eldritch_keystone_tlhutlh",
            "thaumicadditions:inert_keystone", "thaumcraft:salis_mundus", "thaumcraft:salis_mundus", "thaumcraft:balanced_shard", "thaumcraft:nitor");
    }

    private static List<TcIngredient> ingredients(String... ids) {
        return java.util.Arrays.stream(ids).map(id -> id == null ? TcIngredient.EMPTY : TcIngredient.item(id)).toList();
    }

    private static void regular(String key, int vis, String output, boolean shard, String... inputs) {
        register(new InfuserRecipe("thaumicadditions:" + key, false, vis, 0, TcStack.of(output), ingredients(inputs), shard));
    }

    private static void dark(String key, int vis, int taint, String output, String... inputs) {
        register(new InfuserRecipe("thaumicadditions:" + key, true, vis, taint, TcStack.of(output), ingredients(inputs), false));
    }
}
