package thaumicadditions.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.registry.ModItems;

public final class VoidChestLoot {
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static boolean initialized;

    private VoidChestLoot() {
    }

    public static synchronized void register(int chance, Supplier<? extends Item> item, int maxCount, int maxStacks) {
        if (chance < 1 || chance > 100 || maxCount < 1 || maxStacks < 1) {
            throw new IllegalArgumentException("Invalid void chest loot limits");
        }
        ENTRIES.add(new Entry(chance, item, maxCount, maxStacks));
    }

    private static void init() {
        if (!initialized) {
            initialized = true;
            register(5, ModItems.ELDRITCH_STONE, 16, 4);
            register(6, ModItems.INERT_KEYSTONE, 1, 1);
            register(3, thaumcraft.registry.ModItems.VOID_SEED, 6, 5);
            register(6, thaumcraft.registry.ModItems.VOID_METAL_INGOT, 6, 3);
            register(2, ModItems.DARK_GENERATOR, 1, 1);
        }
    }

    public static synchronized List<ItemStack> roll(RandomSource random, int slots) {
        init();
        int[] counts = new int[ENTRIES.size()];
        List<ItemStack> result = new ArrayList<>(slots);
        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = ItemStack.EMPTY;
            for (int index = 0; index < ENTRIES.size(); index++) {
                Entry entry = ENTRIES.get(index);
                if (counts[index] < entry.maxStacks && random.nextInt(100) < entry.chance - 1) {
                    Item item = entry.item.get();
                    stack = new ItemStack(item, 1 + random.nextInt(Math.min(entry.maxCount, item.getDefaultMaxStackSize())));
                    counts[index]++;
                    break;
                }
            }
            result.add(stack);
        }
        return result;
    }

    private record Entry(int chance, Supplier<? extends Item> item, int maxCount, int maxStacks) {
    }
}
