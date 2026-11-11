package thaumicadditions.research;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.crafting.TcStack;
import thaumcraft.research.ResearchCategories;
import thaumcraft.research.ResearchItem;
import thaumcraft.research.ResearchPage;
import thaumicadditions.crafting.ModRecipes;

public final class ModResearch {
    private static final String CATEGORY = "THAUMICADDITIONS";

    private ModResearch() {
    }

    public static void init(Aspect stone, Aspect grain) {
        ResearchCategories.registerCategory(CATEGORY,
            ResourceLocation.parse("thaumicadditions:textures/thaumonomicon/tc_tab_icon.png"),
            ResourceLocation.parse("thaumicadditions:textures/thaumonomicon/tc_tab_background.png"));
        ModRecipes.init();
        new ResearchItem("GRANUM", CATEGORY, new AspectList(), -6, -6, 1,
            ResourceLocation.parse("thaumicadditions:textures/aspects/colored/granum.png"))
            .setAutoUnlock().setPages(text("GRANUM"), ResearchPage.aspects(new AspectList().add(grain, 1)))
            .registerResearchItem();
        new ResearchItem("SAXUM", CATEGORY, new AspectList(), -7, -6, 1,
            ResourceLocation.parse("thaumicadditions:textures/aspects/colored/saxum.png"))
            .setAutoUnlock().setPages(text("SAXUM"), ResearchPage.aspects(new AspectList().add(stone, 1)))
            .registerResearchItem();
        research("ADAMINITECRAFTING", new AspectList().add(Aspect.ENTROPY, 10).add(Aspect.MAGIC, 6)
            .add(Aspect.TRAP, 8).add(stone, 4), 2, 2, 3, ModRecipes.ADAMINITE)
            .setParents("THAUMIUM").setSpecial().setWarp(5)
            .setPages(text("ADAMINITECRAFTING"), ResearchPage.infusion(ModRecipes.ADAMINITE))
            .registerResearchItem();
        research("MITHRILLIUMCRAFTING", new AspectList().add(Aspect.ELDRITCH, 4).add(Aspect.TOOL, 8),
            0, -6, 2, ModRecipes.MITHRILLIUM)
            .setParents("THAUMIUM").setSpecial()
            .setPages(text("MITHRILLIUMCRAFTING"), ResearchPage.infusion(ModRecipes.MITHRILLIUM))
            .registerResearchItem();
        research("CAP_ADAMINITE", new AspectList().add(Aspect.MAGIC, 6).add(Aspect.FIRE, 5),
            4, 0, 2, ModRecipes.ADAMINITE_CAP)
            .setParents("ADAMINITECRAFTING")
            .setPages(text("CAP_ADAMINITE"), ResearchPage.infusion(ModRecipes.ADAMINITE_CAP_INERT, ModRecipes.ADAMINITE_CAP))
            .registerResearchItem();
        research("CAP_MITHRILLIUM", new AspectList().add(Aspect.ELDRITCH, 4).add(Aspect.TOOL, 8),
            -2, -4, 2, ModRecipes.MITHRILLIUM_CAP)
            .setParents("MITHRILLIUMCRAFTING")
            .setPages(text("CAP_MITHRILLIUM"), ResearchPage.infusion(ModRecipes.MITHRILLIUM_CAP_INERT, ModRecipes.MITHRILLIUM_CAP))
            .registerResearchItem();
        research("ROD_ADAMINITEWOOD", ModRecipes.primals(16).add(Aspect.POISON, 4).add(Aspect.AURA, 6),
            4, 1, 2, ModRecipes.ADAMINITEWOOD_ROD)
            .setParents("ADAMINITECRAFTING")
            .setPages(text("ROD_ADAMINITEWOOD"), ResearchPage.infusion(ModRecipes.ADAMINITEWOOD_ROD))
            .registerResearchItem();
        research("ROD_ADAMINITEWOOD_staff", ModRecipes.primals(10).add(Aspect.POISON, 8).add(Aspect.AURA, 12)
            .add(Aspect.TOOL, 8).add(Aspect.CRAFT, 6), 4, 2, 2, ModRecipes.ADAMINITEWOOD_STAFF)
            .setParentsHidden("ROD_ADAMINITEWOOD", "SCEPTRE")
            .setPages(text("ROD_ADAMINITEWOOD_staff"), ResearchPage.arcane(ModRecipes.ADAMINITEWOOD_STAFF))
            .registerResearchItem();
        research("ADAMINITESWORD", new AspectList().add(Aspect.MAGIC, 6).add(Aspect.WEAPON, 16)
            .add(Aspect.TOOL, 6).add(Aspect.CRAFT, 2), 3, -1, 3, ModRecipes.ADAMINITE_SWORD)
            .setParents("ADAMINITECRAFTING")
            .setPages(text("ADAMINITESWORD"), ResearchPage.infusion(ModRecipes.ADAMINITE_SWORD))
            .registerResearchItem();
        research("ADAMINITEARMOR", ModRecipes.primals(8).add(Aspect.AURA, 12),
            2, 4, 2, ModRecipes.ADAMINITE_CHESTPLATE)
            .setParents("ADAMINITECRAFTING")
            .setPages(text("ADAMINITEARMOR"), ResearchPage.infusion(
                ModRecipes.ADAMINITE_HELMET, ModRecipes.ADAMINITE_CHESTPLATE,
                ModRecipes.ADAMINITE_LEGGINGS, ModRecipes.ADAMINITE_BOOTS))
            .registerResearchItem();
        research("SINGULARITYCORE", new AspectList().add(Aspect.ENTROPY, 8).add(Aspect.LIGHT, 4).add(Aspect.DARKNESS, 6),
            1, -4, 2, ModRecipes.SINGULARITY_CORE)
            .setParents("MITHRILLIUMCRAFTING")
            .setPages(text("SINGULARITYCORE"), ResearchPage.infusion(ModRecipes.SINGULARITY_CORE))
            .registerResearchItem();
        research("ENCHANTEDWOOD", ModRecipes.primals(3).add(Aspect.MAGIC, 6).add(Aspect.AURA, 2),
            3, -8, 1, ModRecipes.ENCHANTED_GREATWOOD)
            .setSpecial()
            .setPages(text("ENCHANTEDWOOD"), ResearchPage.infusion(ModRecipes.ENCHANTED_GREATWOOD, ModRecipes.ENCHANTED_SILVERWOOD))
            .registerResearchItem();
        research("TAINTEDSEEDS", new AspectList().add(Aspect.TAINT, 4).add(Aspect.MAGIC, 3)
            .add(Aspect.ELDRITCH, 2).add(Aspect.CROP, 5), 0, -2, 1, "thaumicadditions:tainted_seed")
            .setPages(text("TAINTEDSEEDS"), ResearchPage.crucible("thaumicadditions:tainted_seed"))
            .registerResearchItem();
        research("VISTRANSPORTEVOLVED", ModRecipes.primals(5).add(Aspect.MAGIC, 2)
            .add(Aspect.MOTION, 3).add(Aspect.WATER, 2), 3, -7, 1, "thaumicadditions:vis_conduit")
            .setParents("ENCHANTEDWOOD").setSecondary()
            .setPages(text("VISTRANSPORTEVOLVED"), ResearchPage.arcane("thaumicadditions:vis_conduit"))
            .registerResearchItem();
        research("INFUSER", ModRecipes.primals(7).add(Aspect.MAGIC, 4), 3, -3, 2, "thaumicadditions:infuser")
            .setPages(text("INFUSER"), ResearchPage.arcane("thaumicadditions:infuser"))
            .registerResearchItem();
        research("INFUSERDARK", ModRecipes.primals(9).add(Aspect.MAGIC, 4), 3, -4, 2, "thaumicadditions:dark_infuser")
            .setParents("INFUSER")
            .setWarp(3).setPages(text("INFUSERDARK"))
            .registerResearchItem();
        research("ARCANETRANSPORTER", new AspectList().add(Aspect.MOTION, 4).add(Aspect.TRAVEL, 3),
            3, -9, -1, "thaumicadditions:arcane_transporter")
            .setParents("THAUMIUM")
            .setPages(text("ARCANETRANSPORTER"), ResearchPage.infusion("thaumicadditions:arcane_transporter"))
            .registerResearchItem();
        research("TRANSMUTATIONSTONEARCANE", ModRecipes.primals(10), 0, -4, 3, "thaumicadditions:arcane_stone")
            .setParentsHidden("MITHRILLIUMCRAFTING")
            .setPages(text("TRANSMUTATIONSTONEARCANE"), ResearchPage.arcane("thaumicadditions:arcane_stone"))
            .registerResearchItem();
        research("VISCONDENSATOR", ModRecipes.primals(3), 2, -6, -5, "thaumicadditions:old_condensator")
            .setParents("ENCHANTEDWOOD")
            .setPages(text("VISCONDENSATOR"), ResearchPage.arcane("thaumicadditions:old_condensator"))
            .registerResearchItem();
        research("INFUSION_ACTIVATOR", ModRecipes.primals(6).add(Aspect.MAGIC, 3).add(Aspect.CRAFT, 5),
            2, -5, -5, "thaumicadditions:infuser_activator")
            .setParents("INFUSION").setSpecial()
            .setPages(text("INFUSION_ACTIVATOR"), ResearchPage.infusion("thaumicadditions:infuser_activator"))
            .registerResearchItem();
        research("ARCANEDICE", ModRecipes.primals(6), 1, -5, -1, "thaumicadditions:arcane_dice")
            .setParentsHidden("MITHRILLIUMCRAFTING", "PRIMPEARL").setWarp(3)
            .setPages(text("ARCANEDICE"), ResearchPage.arcane("thaumicadditions:arcane_dice"))
            .registerResearchItem();
        research("ARCANESEAL", ModRecipes.primals(6), 6, -4, 2, "thaumicadditions:seal_item")
            .setPages(text("ARCANESEAL"), ResearchPage.infusion("thaumicadditions:seal_item"))
            .registerResearchItem();
        research("SEALSYMBOLING", new AspectList().add(Aspect.MAGIC, 4).add(Aspect.GREED, 2),
            6, -2, 2, "thaumicadditions:seal_symbol")
            .setParents("ARCANESEAL")
            .setPages(text("SEALSYMBOLING"), ResearchPage.infusion("thaumicadditions:seal_symbol"))
            .registerResearchItem();
        research("SEALSYMBOLLER", new AspectList().add(Aspect.MAGIC, 4).add(Aspect.GREED, 2),
            7, -3, 2, "thaumicadditions:symboller")
            .setParents("SEALSYMBOLING")
            .setPages(text("SEALSYMBOLLER"), ResearchPage.infusion("thaumicadditions:symboller"))
            .registerResearchItem();
        research("WANDFOCUSPROJECTILE", ModRecipes.primals(1), 1, -7, 0, "thaumicadditions:foci_projectile")
            .setPages(text("WANDFOCUSPROJECTILE"), ResearchPage.arcane("thaumicadditions:foci_projectile"))
            .registerResearchItem();
        research("WANDFOCUSXP", ModRecipes.primals(2).add(Aspect.MIND, 4), 2, -7, -2, "thaumicadditions:foci_xp")
            .setPages(text("WANDFOCUSXP"), ResearchPage.infusion("thaumicadditions:foci_xp"))
            .registerResearchItem();
        research("SINGULARITY", new AspectList().add(Aspect.ENTROPY, 8).add(Aspect.FIRE, 8).add(Aspect.LIGHT, 4),
            0, 0, 2, "thaumicadditions:singularity")
            .setParents("SINGULARITYCORE")
            .setPages(text("SINGULARITY"), ResearchPage.infusion("thaumicadditions:singularity"))
            .registerResearchItem();
    }

    private static ResearchItem research(String key, AspectList aspects, int column, int row, int complexity, String icon) {
        return new ResearchItem(key, CATEGORY, aspects, column, row, complexity, TcStack.of(icon));
    }

    private static ResearchPage text(String key) {
        return ResearchPage.text("tc.research_page.thaumicadditions." + key);
    }
}
