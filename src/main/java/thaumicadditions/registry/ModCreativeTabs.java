package thaumicadditions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.wand.WandCap;
import thaumcraft.wand.WandRod;
import thaumcraft.wand.Wands;
import thaumicadditions.ThaumicAdditions;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThaumicAdditions.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THAUMIC_ADDITIONS =
        TABS.register("thaumicadditions", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.thaumicadditions"))
            .icon(() -> ModItems.ADAMINITE_INGOT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                ModItems.ITEMS.getEntries().forEach(item -> {
                    if (item != ModItems.RETURNER && item != ModItems.ELDRITCH_LOCK) {
                        output.accept(item.get());
                    }
                });
                for (var aspect : thaumcraft.aspect.Aspect.values()) {
                    output.accept(thaumicadditions.item.SealSymbolItem.symbol(aspect.getTag()));
                }
                ItemStack infiniteSingularity = new ItemStack(ModItems.SINGULARITY.get());
                infiniteSingularity.set(ModDataComponents.CREATIVE_DISPENSER.get(), true);
                output.accept(infiniteSingularity);
                ItemStack lootChest = new ItemStack(ModItems.VOID_CHEST.get());
                lootChest.set(ModDataComponents.VOID_CHEST_LOOT.get(), true);
                lootChest.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                    Component.translatable("item.thaumicadditions.void_chest_with_loot"));
                output.accept(lootChest);
                output.accept(filledWand(ModWands.ADAMINITE_CAP, ModWands.ADAMINITEWOOD_ROD));
                output.accept(filledWand(ModWands.MITHRILLIUM_CAP, ModWands.ADAMINITEWOOD_ROD));
                output.accept(filledWand(ModWands.ADAMINITE_CAP, ModWands.ADAMINITEWOOD_STAFF));
                output.accept(filledWand(ModWands.MITHRILLIUM_CAP, ModWands.ADAMINITEWOOD_STAFF));
            })
            .build());

    private ModCreativeTabs() {
    }

    private static ItemStack filledWand(WandCap cap, WandRod rod) {
        ItemStack stack = Wands.create(cap, rod, false);
        Wands.fill(stack);
        return stack;
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
