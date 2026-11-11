package thaumicadditions.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import thaumicadditions.ThaumicAdditions;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class ModCapabilities {
    private ModCapabilities() {
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SYMBOLLER.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.VOID_CHEST.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.INFUSER.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.HYPER_NODE_CREATOR.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.RESTORER.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.DARK_GENERATOR.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.THAUMIC_GENERATOR.get(), (machine, side) -> machine.energyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CRYSTALLIZER.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CONDENSER.get(), SidedInvWrapper::new);
    }
}
