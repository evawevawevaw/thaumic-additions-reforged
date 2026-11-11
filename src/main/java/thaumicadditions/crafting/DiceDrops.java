package thaumicadditions.crafting;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class DiceDrops {
    private static final Set<ResourceLocation> DROPS = new LinkedHashSet<>(List.of(ResourceLocation.parse("thaumicadditions:wand_hallelujah")));

    private DiceDrops() {
    }

    public static synchronized void register(Item item) {
        if (item == Items.AIR) {
            throw new IllegalArgumentException("Air cannot be a dice prize");
        }
        register(BuiltInRegistries.ITEM.getKey(item));
    }

    public static synchronized void register(ResourceLocation item) {
        DROPS.add(java.util.Objects.requireNonNull(item));
    }

    public static synchronized List<ResourceLocation> all() {
        return List.copyOf(DROPS);
    }

    public static synchronized ItemStack roll(RandomSource random) {
        List<Item> available = DROPS.stream().filter(BuiltInRegistries.ITEM::containsKey)
            .map(BuiltInRegistries.ITEM::get).filter(item -> item != Items.AIR).toList();
        return available.isEmpty() ? ItemStack.EMPTY : new ItemStack(available.get(random.nextInt(available.size())));
    }
}
