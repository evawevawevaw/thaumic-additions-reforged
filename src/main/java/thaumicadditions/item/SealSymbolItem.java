package thaumicadditions.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.aspect.Aspect;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.registry.ModItems;

public final class SealSymbolItem extends Item {
    public SealSymbolItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    public static Aspect aspect(ItemStack stack) {
        String tag = stack.get(ModDataComponents.SYMBOL_ASPECT.get());
        return tag == null ? null : Aspect.getAspect(tag);
    }

    public static ItemStack symbol(String tag) {
        ItemStack stack = new ItemStack(ModItems.SEAL_SYMBOL.get());
        stack.set(ModDataComponents.SYMBOL_ASPECT.get(), tag);
        return stack;
    }

}
