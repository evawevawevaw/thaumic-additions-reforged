package thaumicadditions.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.crafting.InfusionRecipe;
import thaumcraft.crafting.CrucibleRecipe;
import thaumcraft.crafting.ShapedArcaneRecipe;
import thaumcraft.crafting.TcIngredient;
import thaumcraft.crafting.TcRecipes;
import thaumcraft.crafting.TcStack;

public final class ModRecipes {
    public static final String ADAMINITE = "thaumicadditions:adaminite_ingot";
    public static final String MITHRILLIUM = "thaumicadditions:mithrillium_ingot";
    public static final String ADAMINITE_CAP_INERT = "thaumicadditions:cap_adaminite_inert";
    public static final String ADAMINITE_CAP = "thaumicadditions:cap_adaminite";
    public static final String MITHRILLIUM_CAP_INERT = "thaumicadditions:cap_mithrillium_inert";
    public static final String MITHRILLIUM_CAP = "thaumicadditions:cap_mithrillium";
    public static final String ADAMINITEWOOD_ROD = "thaumicadditions:wand_rod_adaminitewood";
    public static final String ADAMINITEWOOD_STAFF = "thaumicadditions:wand_staff_adaminitewood";

    public static final String ADAMINITE_SWORD = "thaumicadditions:adaminite_sword";
    public static final String ADAMINITE_HELMET = "thaumicadditions:adaminite_helmet";
    public static final String ADAMINITE_CHESTPLATE = "thaumicadditions:adaminite_chestplate";
    public static final String ADAMINITE_LEGGINGS = "thaumicadditions:adaminite_leggings";
    public static final String ADAMINITE_BOOTS = "thaumicadditions:adaminite_boots";

    public static final String SINGULARITY_CORE = "thaumicadditions:singcore";
    public static final String ENCHANTED_GREATWOOD = "thaumicadditions:enchanted_greatwood";
    public static final String ENCHANTED_SILVERWOOD = "thaumicadditions:enchanted_silverwood";

    private ModRecipes() {
    }

    public static void init() {
        initEquipment();
        initTransportDevices();
        infusion("thaumicadditions:singularity", "SINGULARITY", 2, 4, new AspectList().add(Aspect.FIRE, 8)
            .add(Aspect.ENTROPY, 8).add(Aspect.MAGIC, 4).add(Aspect.DARKNESS, 8).add(Aspect.ENERGY, 6), SINGULARITY_CORE,
            List.of("thaumcraft:alumentum", "thaumcraft:nitor", "thaumcraft:nitor", "thaumcraft:alumentum"));
        TcIngredient slab = TcIngredient.item("minecraft:smooth_stone_slab");
        TcIngredient iron = TcIngredient.item("minecraft:iron_ingot");
        TcRecipes.register(new ShapedArcaneRecipe("thaumicadditions:infuser", "INFUSER", TcStack.of("thaumicadditions:infuser"),
            primals(12), 3, 3, List.of(slab, slab, slab, iron, TcIngredient.item("thaumcraft:primal_charm"), iron, iron, iron, iron)));
        TcIngredient wood = TcIngredient.tag("thaumicadditions:enchanted_greatwood");
        TcIngredient glass = TcIngredient.tag("c:glass_panes");
        TcRecipes.register(new ShapedArcaneRecipe("thaumicadditions:vis_conduit", "VISTRANSPORTEVOLVED",
            TcStack.of("thaumicadditions:vis_conduit", 16), primals(5), 3, 3,
            List.of(wood, glass, wood, glass, TcIngredient.EMPTY, glass, wood, glass, wood)));
        TcIngredient goldFocus = TcIngredient.tag("c:ingots/gold");
        TcIngredient ironFocus = TcIngredient.tag("c:ingots/iron");
        TcRecipes.register(new ShapedArcaneRecipe("thaumicadditions:foci_projectile", "WANDFOCUSPROJECTILE",
            TcStack.of("thaumicadditions:foci_projectile"), primals(80), 3, 3,
            List.of(goldFocus, ironFocus, goldFocus, ironFocus, TcIngredient.item("thaumcraft:focus_frost"), ironFocus,
                goldFocus, ironFocus, goldFocus)));
        infusion("thaumicadditions:foci_xp", "WANDFOCUSXP", 1, 10, primals(7).add(Aspect.MIND, 64),
            "thaumcraft:brain_jar", List.of("thaumcraft:zombie_brain", "thaumcraft:salis_mundus",
                "thaumcraft:zombie_brain", "thaumcraft:salis_mundus", "thaumcraft:zombie_brain",
                "thaumcraft:salis_mundus", "thaumcraft:zombie_brain", "thaumcraft:salis_mundus"));
        infusion("thaumicadditions:seal_item", "ARCANESEAL", 1, 2, primals(2), "minecraft:white_wool",
            Collections.nCopies(4, "minecraft:gold_ingot"));
        infusion("thaumicadditions:seal_symbol", "SEALSYMBOLING", 1, 0, primals(1), "minecraft:blaze_powder",
            Collections.nCopies(3, "minecraft:gold_nugget"));
        infusion("thaumicadditions:symboller", "SEALSYMBOLING", 1, 5, primals(4), "thaumicadditions:seal_symbol",
            List.of("minecraft:gold_ingot", "minecraft:gold_ingot", "minecraft:gold_ingot", "minecraft:gold_ingot",
                "minecraft:diamond", "thaumcraft:salis_mundus"));
        initMaterials();
        TcRecipes.register(new CrucibleRecipe("thaumicadditions:tainted_seed", "TAINTEDSEEDS",
            TcStack.of("thaumicadditions:tainted_seed"), TcIngredient.item("minecraft:wheat_seeds"),
            new AspectList().add(Aspect.TAINT, 16)));
        infusion(ADAMINITE, "ADAMINITECRAFTING", 1, 16,
            new AspectList().add(Aspect.MAGIC, 16).add(Aspect.EARTH, 64).add(Aspect.FIRE, 128),
            "thaumcraft:thaumium_ingot", List.of(
                "thaumcraft:enchanted_fabric", "thaumcraft:enchanted_fabric", "thaumcraft:amber", "thaumcraft:amber",
                "minecraft:diamond", "minecraft:diamond", "minecraft:nether_star", "minecraft:nether_star"));
        infusion(MITHRILLIUM, "MITHRILLIUMCRAFTING", 2, 6, mithrilliumAspects(),
            "thaumcraft:thaumium_ingot", List.of(
                "minecraft:diamond", "minecraft:diamond", "thaumcraft:salis_mundus", "thaumcraft:salis_mundus",
                "thaumcraft:enchanted_fabric", "thaumcraft:enchanted_fabric", "thaumcraft:amber", "thaumcraft:amber"));
        infusion(ADAMINITE_CAP_INERT, "CAP_ADAMINITE", 1, 8,
            new AspectList().add(Aspect.MAGIC, 32).add(Aspect.FIRE, 32).add(Aspect.ELDRITCH, 8),
            "thaumcraft:thaumium_wand_cap", List.of(
                ADAMINITE, ADAMINITE, "minecraft:diamond", "minecraft:diamond", "thaumcraft:zombie_brain"));
        infusion(ADAMINITE_CAP, "CAP_ADAMINITE", 1, 8,
            new AspectList().add(Aspect.MAGIC, 32).add(Aspect.FIRE, 32).add(Aspect.AURA, 16),
            ADAMINITE_CAP_INERT, Collections.nCopies(8, "thaumcraft:salis_mundus"));
        infusion(MITHRILLIUM_CAP_INERT, "CAP_MITHRILLIUM", 1, 6, mithrilliumAspects(),
            "thaumcraft:thaumium_wand_cap", List.of(MITHRILLIUM, MITHRILLIUM, "minecraft:diamond"));
        infusion(MITHRILLIUM_CAP, "CAP_MITHRILLIUM", 1, 6, mithrilliumAspects(),
            MITHRILLIUM_CAP_INERT, Collections.nCopies(8, "thaumcraft:salis_mundus"));
        List<String> rodComponents = new ArrayList<>(List.of(ADAMINITE, ADAMINITE, "minecraft:diamond", "minecraft:diamond"));
        rodComponents.addAll(Collections.nCopies(8, "thaumcraft:salis_mundus"));
        infusion(ADAMINITEWOOD_ROD, "ROD_ADAMINITEWOOD", 1, 8,
            new AspectList().add(Aspect.MAGIC, 64).add(Aspect.FIRE, 32).add(Aspect.POISON, 32).add(Aspect.AURA, 12),
            "thaumcraft:silverwood_rod", rodComponents);
        TcRecipes.register(new ShapedArcaneRecipe(ADAMINITEWOOD_STAFF, "ROD_ADAMINITEWOOD_staff",
            TcStack.of(ADAMINITEWOOD_STAFF), primals(500), 3, 3, List.of(
                TcIngredient.EMPTY, TcIngredient.EMPTY, TcIngredient.item("thaumcraft:primal_charm"),
                TcIngredient.EMPTY, TcIngredient.item(ADAMINITEWOOD_ROD), TcIngredient.EMPTY,
                TcIngredient.item(ADAMINITEWOOD_ROD), TcIngredient.EMPTY, TcIngredient.EMPTY)));
    }

    private static void initMaterials() {
        infusion(SINGULARITY_CORE, "SINGULARITYCORE", 1, 4,
            new AspectList().add(Aspect.FIRE, 4).add(Aspect.ENTROPY, 16).add(Aspect.MAGIC, 4)
                .add(Aspect.DARKNESS, 32).add(Aspect.ENERGY, 32).add(Aspect.VOID, 32),
            MITHRILLIUM, List.of("minecraft:obsidian", "minecraft:obsidian", "minecraft:ender_pearl", "thaumcraft:salis_mundus"));
        infusion(ENCHANTED_GREATWOOD, "ENCHANTEDWOOD", 1, 0, primals(4),
            "thaumcraft:greatwood_log", Collections.nCopies(4, "minecraft:oak_log"));
        infusion(ENCHANTED_SILVERWOOD, "ENCHANTEDWOOD", 1, 1, primals(8),
            "thaumcraft:silverwood_log", Collections.nCopies(4, ENCHANTED_GREATWOOD));
    }

    private static void initEquipment() {
        List<String> components = List.of(ADAMINITE, ADAMINITE, "minecraft:diamond", "minecraft:diamond");
        infusion(ADAMINITE_SWORD, "ADAMINITESWORD", 1, 8, new AspectList().add(Aspect.MAGIC, 64),
            "thaumcraft:thaumium_sword", components);
        List<String> helmetComponents = new ArrayList<>();
        helmetComponents.add("thaumcraft:goggles_of_revealing");
        helmetComponents.addAll(components);
        infusion(ADAMINITE_HELMET, "ADAMINITEARMOR", 1, 16, armorAspects(),
            "thaumcraft:thaumium_helmet", helmetComponents);
        infusion(ADAMINITE_CHESTPLATE, "ADAMINITEARMOR", 1, 16, armorAspects(),
            "thaumcraft:thaumium_chestplate", components);
        infusion(ADAMINITE_LEGGINGS, "ADAMINITEARMOR", 1, 16, armorAspects(),
            "thaumcraft:thaumium_leggings", components);
        infusion(ADAMINITE_BOOTS, "ADAMINITEARMOR", 1, 16, armorAspects(),
            "thaumcraft:thaumium_boots", components);
    }

    private static void initTransportDevices() {
        infusion("thaumicadditions:arcane_transporter", "ARCANETRANSPORTER", 1, 6,
            primals(8).add(Aspect.MOTION, 16), "minecraft:chest", Collections.nCopies(8, "thaumcraft:thaumium_ingot"));
        infusion("thaumicadditions:infuser_activator", "INFUSION_ACTIVATOR", 1, 8,
            primals(32).add(Aspect.MAGIC, 32).add(Aspect.CRAFT, 15), "thaumcraft:primal_charm",
            List.of("thaumcraft:salis_mundus", "minecraft:diamond", "thaumcraft:salis_mundus", "minecraft:iron_ingot",
                "thaumcraft:salis_mundus", "minecraft:iron_ingot", "thaumcraft:salis_mundus", "minecraft:gold_ingot"));
        TcIngredient mithrillium = TcIngredient.tag("c:ingots/mithrillium");
        TcIngredient arcaneStone = TcIngredient.item("thaumcraft:arcane_stone");
        TcIngredient pearl = TcIngredient.item("thaumcraft:primordial_pearl");
        TcRecipes.register(new ShapedArcaneRecipe("thaumicadditions:arcane_stone", "TRANSMUTATIONSTONEARCANE",
            TcStack.of("thaumicadditions:arcane_stone"), primals(113), 3, 3,
            List.of(mithrillium, arcaneStone, mithrillium, arcaneStone, pearl, arcaneStone, mithrillium, arcaneStone, mithrillium)));
        TcIngredient voidMetal = TcIngredient.tag("c:ingots/void_metal");
        TcRecipes.register(new ShapedArcaneRecipe("thaumicadditions:arcane_dice", "ARCANEDICE", TcStack.of("thaumicadditions:arcane_dice"),
            primals(150), 3, 3, List.of(mithrillium, voidMetal, mithrillium, voidMetal, pearl, voidMetal, mithrillium, voidMetal, mithrillium)));
        TcIngredient greatwood = TcIngredient.tag("thaumicadditions:enchanted_greatwood");
        TcIngredient silverwood = TcIngredient.tag("thaumicadditions:enchanted_silverwood");
        TcRecipes.register(new ShapedArcaneRecipe("thaumicadditions:old_condensator", "VISCONDENSATOR",
            TcStack.of("thaumicadditions:old_condensator"), primals(4), 3, 3,
            List.of(greatwood, TcIngredient.EMPTY, greatwood, greatwood, silverwood, greatwood,
                greatwood, TcIngredient.tag("c:ingots/iron"), greatwood)));
    }

    private static AspectList armorAspects() {
        return new AspectList().add(Aspect.MAGIC, 64).add(Aspect.FIRE, 32).add(Aspect.HEAL, 12).add(Aspect.LIFE, 16);
    }

    public static AspectList primals(int amount) {
        AspectList aspects = new AspectList();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            aspects.add(aspect, amount);
        }
        return aspects;
    }

    private static AspectList mithrilliumAspects() {
        return new AspectList().add(Aspect.FIRE, 8).add(Aspect.ENTROPY, 8).add(Aspect.MAGIC, 4)
            .add(Aspect.ENERGY, 6).add(Aspect.ELDRITCH, 3);
    }

    private static void infusion(String result, String research, int count, int instability,
        AspectList aspects, String central, List<String> components) {
        TcRecipes.register(new InfusionRecipe(result, research, TcStack.of(result, count), instability, aspects,
            TcIngredient.item(central), components.stream().map(TcIngredient::item).toList()));
    }
}
