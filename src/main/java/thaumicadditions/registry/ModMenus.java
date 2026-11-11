package thaumicadditions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.menu.InfuserMenu;
import thaumicadditions.menu.InfusionBookMenu;

public final class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThaumicAdditions.MOD_ID);
    public static final DeferredHolder<MenuType<?>, MenuType<InfuserMenu>> INFUSER = MENUS.register("infuser",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new InfuserMenu(id, inventory, buffer.readBlockPos(), buffer.readBoolean())));

    public static final DeferredHolder<MenuType<?>, MenuType<InfusionBookMenu>> INFUSION_BOOK = MENUS.register("infusion_book",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new InfusionBookMenu(id, inventory, buffer.readBoolean())));

    public static final DeferredHolder<MenuType<?>, MenuType<thaumicadditions.menu.CondenserMenu>> CONDENSER = MENUS.register("old_condensator",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new thaumicadditions.menu.CondenserMenu(id, inventory, buffer.readBlockPos())));

    public static final DeferredHolder<MenuType<?>, MenuType<thaumicadditions.menu.CrystallizerMenu>> CRYSTALLIZER = MENUS.register("thaumic_crystallizer",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new thaumicadditions.menu.CrystallizerMenu(id, inventory, buffer.readBlockPos())));

    public static final DeferredHolder<MenuType<?>, MenuType<thaumicadditions.menu.ThaumicGeneratorMenu>> THAUMIC_GENERATOR = MENUS.register("thaumic_generator",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new thaumicadditions.menu.ThaumicGeneratorMenu(id, inventory, buffer.readBlockPos())));

    public static final DeferredHolder<MenuType<?>, MenuType<thaumicadditions.menu.DarkGeneratorMenu>> DARK_GENERATOR = MENUS.register("dark_generator",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new thaumicadditions.menu.DarkGeneratorMenu(id, inventory, buffer.readBlockPos())));

    public static final DeferredHolder<MenuType<?>, MenuType<thaumicadditions.menu.RestorerMenu>> RESTORER = MENUS.register("thaumic_restorer",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new thaumicadditions.menu.RestorerMenu(id, inventory, buffer.readBlockPos())));

    public static final DeferredHolder<MenuType<?>, MenuType<thaumicadditions.menu.HyperNodeCreatorMenu>> HYPER_NODE_CREATOR = MENUS.register("hyper_node_creator",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new thaumicadditions.menu.HyperNodeCreatorMenu(id, inventory, buffer.readBlockPos())));

    public static final DeferredHolder<MenuType<?>, MenuType<thaumicadditions.menu.VoidChestMenu>> VOID_CHEST = MENUS.register("void_chest",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new thaumicadditions.menu.VoidChestMenu(id, inventory, buffer.readBlockPos())));

    public static final DeferredHolder<MenuType<?>, MenuType<thaumicadditions.menu.SealGlobeMenu>> SEAL_GLOBE = MENUS.register("seal_globe",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new thaumicadditions.menu.SealGlobeMenu(id, inventory, buffer.readBlockPos())));

    private ModMenus() {
    }

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
