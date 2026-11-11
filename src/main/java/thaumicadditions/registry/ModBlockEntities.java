package thaumicadditions.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.blockentity.TaintedCropBlockEntity;
import thaumicadditions.blockentity.MagicTankBlockEntity;
import thaumicadditions.blockentity.OldCrucibleBlockEntity;
import thaumicadditions.blockentity.VisPipeBlockEntity;
import thaumicadditions.blockentity.InfuserBlockEntity;

public final class ModBlockEntities {
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TaintedCropBlockEntity>> TAINTED_CROPS =
        ThaumicAdditions.BLOCK_ENTITIES.register("tainted_crops", () -> BlockEntityType.Builder.of(
            TaintedCropBlockEntity::new, ModBlocks.TAINTED_CROPS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagicTankBlockEntity>> MAGIC_TANK =
        ThaumicAdditions.BLOCK_ENTITIES.register("magic_tank", () -> BlockEntityType.Builder.of(
            MagicTankBlockEntity::new, ModBlocks.TANK_BASIC.get(), ModBlocks.TANK_REINFORCED.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OldCrucibleBlockEntity>> OLD_CRUCIBLE =
        ThaumicAdditions.BLOCK_ENTITIES.register("old_crucible", () -> BlockEntityType.Builder.of(
            OldCrucibleBlockEntity::new, ModBlocks.OLD_CRUCIBLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VisPipeBlockEntity>> VIS_PIPE =
        ThaumicAdditions.BLOCK_ENTITIES.register("vis_pipe", () -> BlockEntityType.Builder.of(
            VisPipeBlockEntity::new, ModBlocks.VIS_CONDUIT.get(), ModBlocks.FILTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserBlockEntity>> INFUSER =
        ThaumicAdditions.BLOCK_ENTITIES.register("infuser", () -> BlockEntityType.Builder.of(
            InfuserBlockEntity::new, ModBlocks.INFUSER.get(), ModBlocks.DARK_INFUSER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.CondenserBlockEntity>> CONDENSER =
        ThaumicAdditions.BLOCK_ENTITIES.register("old_condensator", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.CondenserBlockEntity::new, ModBlocks.OLD_CONDENSATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.CrystallizerBlockEntity>> CRYSTALLIZER =
        ThaumicAdditions.BLOCK_ENTITIES.register("thaumic_crystallizer", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.CrystallizerBlockEntity::new, ModBlocks.THAUMIC_CRYSTALLIZER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.ThaumicGeneratorBlockEntity>> THAUMIC_GENERATOR =
        ThaumicAdditions.BLOCK_ENTITIES.register("thaumic_generator", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.ThaumicGeneratorBlockEntity::new, ModBlocks.THAUMIC_GENERATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.DarkGeneratorBlockEntity>> DARK_GENERATOR =
        ThaumicAdditions.BLOCK_ENTITIES.register("dark_generator", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.DarkGeneratorBlockEntity::new, ModBlocks.DARK_GENERATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.RestorerBlockEntity>> RESTORER =
        ThaumicAdditions.BLOCK_ENTITIES.register("thaumic_restorer", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.RestorerBlockEntity::new, ModBlocks.THAUMIC_RESTORER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.InfusionActivatorBlockEntity>> INFUSION_ACTIVATOR =
        ThaumicAdditions.BLOCK_ENTITIES.register("infuser_activator", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.InfusionActivatorBlockEntity::new, ModBlocks.INFUSION_ACTIVATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.TileBoosterBlockEntity>> TILE_BOOSTER =
        ThaumicAdditions.BLOCK_ENTITIES.register("tile_booster", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.TileBoosterBlockEntity::new, ModBlocks.TILE_BOOSTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.CreativeNodeBlockEntity>> CREATIVE_NODE =
        ThaumicAdditions.BLOCK_ENTITIES.register("creative_node", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.CreativeNodeBlockEntity::new, ModBlocks.CREATIVE_NODE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.HyperNodeCreatorBlockEntity>> HYPER_NODE_CREATOR =
        ThaumicAdditions.BLOCK_ENTITIES.register("hyper_node_creator", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.HyperNodeCreatorBlockEntity::new, ModBlocks.HYPER_NODE_CREATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.VoidChestBlockEntity>> VOID_CHEST =
        ThaumicAdditions.BLOCK_ENTITIES.register("void_chest", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.VoidChestBlockEntity::new, ModBlocks.VOID_CHEST.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.ReturnerBlockEntity>> RETURNER =
        ThaumicAdditions.BLOCK_ENTITIES.register("returner", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.ReturnerBlockEntity::new, ModBlocks.RETURNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.SealBlockEntity>> SEAL =
        ThaumicAdditions.BLOCK_ENTITIES.register("arcane_seal", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.SealBlockEntity::new, ModBlocks.SEAL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<thaumicadditions.blockentity.SymbollerBlockEntity>> SYMBOLLER =
        ThaumicAdditions.BLOCK_ENTITIES.register("symboller", () -> BlockEntityType.Builder.of(
            thaumicadditions.blockentity.SymbollerBlockEntity::new, ModBlocks.SYMBOLLER.get()).build(null));

    private ModBlockEntities() {
    }

    public static void init() {
    }
}
