package thaumicadditions.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import thaumcraft.aspect.AddonAspects;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.aspect.AspectRegistry;
import thaumcraft.aspect.AspectSync;
import thaumicadditions.research.ModResearch;
import thaumicadditions.crafting.InfuserRecipes;

public final class ModAspects {
    private ModAspects() {
    }

    public static void register() {
        thaumcraft.research.Warp.registerItem(ResourceLocation.parse("thaumicadditions:arcane_dice"), 3);
        thaumcraft.research.Warp.registerItem(ResourceLocation.parse("thaumicadditions:adaminite_ingot"), 5);
        thaumcraft.research.Warp.registerItem(ResourceLocation.parse("thaumicadditions:dark_infuser"), 3);
        AddonAspects.addRegistrar(api -> {
            Aspect stone = api.register("saxum", 8421504, new Aspect[]{Aspect.EARTH, Aspect.EARTH}, 1,
                ResourceLocation.parse("thaumicadditions:textures/aspects/mono/saxum.png"));
            Aspect grain = api.register("granum", 15638894, new Aspect[]{Aspect.PLANT, Aspect.LIFE}, 1,
                ResourceLocation.parse("thaumicadditions:textures/aspects/mono/granum.png"));
            ModResearch.init(stone, grain);
            InfuserRecipes.init();
            AspectSync.addRegistrar(registry -> {
                addAspect(registry, Items.WHEAT_SEEDS, grain, 1);
                addStone(registry, Items.STONE, stone);
                addStone(registry, Items.COBBLESTONE, stone);
                registry.register(ModItems.SEAL_GLOBE.get(), new AspectList().add(Aspect.MIND, 6).add(Aspect.MAGIC, 3));
                registry.register(ModItems.INFUSER_BOOK.get(), new AspectList().add(Aspect.MIND, 3).add(Aspect.MAGIC, 3));
                registry.register(ModItems.DARK_INFUSER_BOOK.get(), new AspectList().add(Aspect.MIND, 3).add(Aspect.MAGIC, 3));
                registry.register(ModItems.INERT_KEYSTONE.get(), new AspectList().add(Aspect.ELDRITCH, 3).add(Aspect.MAGIC, 4));
                registry.register(ModItems.DEPLETED_SHARD.get(), new AspectList().add(Aspect.VOID, 4).add(Aspect.MAGIC, 1));
                registry.register(ModItems.TAINTED_ROOTS.get(), new AspectList().add(Aspect.TAINT, 6)
                    .add(Aspect.MAGIC, 6).add(Aspect.HARVEST, 2).add(Aspect.CROP, 3));
            });
        });
    }

    private static void addStone(AspectRegistry registry, Item item, Aspect stone) {
        addAspect(registry, item, stone, 4);
    }

    private static void addAspect(AspectRegistry registry, Item item, Aspect aspect, int amount) {
        AspectList existing = registry.getObjectTags(item);
        AspectList aspects = existing == null ? new AspectList() : existing.copy();
        registry.register(item, aspects.add(aspect, amount));
    }
}
