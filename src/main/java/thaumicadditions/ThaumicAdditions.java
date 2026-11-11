package thaumicadditions;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.aspect.AddonAspects;
import thaumicadditions.registry.ModAspects;
import thaumicadditions.registry.ModBlocks;
import thaumicadditions.registry.ModBlockEntities;
import thaumicadditions.registry.ModArmorMaterials;
import thaumicadditions.registry.ModCreativeTabs;
import thaumicadditions.registry.ModDataComponents;
import thaumicadditions.registry.ModItems;
import thaumicadditions.registry.ModWands;
import thaumicadditions.registry.ModSounds;
import thaumicadditions.registry.ModMenus;

@Mod("thaumicadditions")
public final class ThaumicAdditions {
    public static final String MOD_ID = "thaumicadditions";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);

    public ThaumicAdditions(IEventBus modEventBus, net.neoforged.fml.ModContainer container) {
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, thaumicadditions.config.ModConfig.SPEC);
        thaumicadditions.registry.ModFluids.register(modEventBus);
        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) ->
            event.enqueueWork(thaumicadditions.registry.ModFocusUpgrades::init));
        ModSounds.register(modEventBus);
        thaumicadditions.registry.ModParticles.register(modEventBus);
        ModMenus.register(modEventBus);
        thaumicadditions.registry.ModRecipeSerializers.register(modEventBus);
        thaumicadditions.registry.ModWorldGen.register(modEventBus);
        ModBlocks.init();
        ModBlockEntities.init();
        ModAspects.register();
        ModWands.init();
        ModDataComponents.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModItems.register(modEventBus);
        thaumicadditions.registry.ModEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        LogUtils.getLogger().info("Thaumic Additions initialized; Thaumcraft API: {}", AddonAspects.class.getName());
    }
}
