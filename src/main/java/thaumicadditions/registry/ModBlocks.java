package thaumicadditions.registry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.block.TaintedCropBlock;
import thaumicadditions.block.MagicStorageBlock;
import thaumicadditions.block.VisPipeBlock;
import thaumicadditions.block.InfuserBlock;

public final class ModBlocks {
    public static final DeferredBlock<TaintedCropBlock> TAINTED_CROPS = ThaumicAdditions.BLOCKS.registerBlock(
        "tainted_crops", TaintedCropBlock::new, BlockBehaviour.Properties.of().noCollission()
            .noOcclusion().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<MagicStorageBlock> TANK_BASIC = ThaumicAdditions.BLOCKS.register("tank_basic",
        () -> new MagicStorageBlock(BlockBehaviour.Properties.of().strength(0.3F, 1.0F).noOcclusion()
            .sound(ModSounds.WOOD_GLASS), false, false));
    public static final DeferredBlock<MagicStorageBlock> TANK_REINFORCED = ThaumicAdditions.BLOCKS.register("tank_reinforced",
        () -> new MagicStorageBlock(BlockBehaviour.Properties.of().strength(0.3F, 1.0F).noOcclusion()
            .sound(SoundType.METAL), true, false));
    public static final DeferredBlock<MagicStorageBlock> OLD_CRUCIBLE = ThaumicAdditions.BLOCKS.register("old_crucible",
        () -> new MagicStorageBlock(BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.METAL), false, true));

    public static final DeferredBlock<VisPipeBlock> VIS_CONDUIT = ThaumicAdditions.BLOCKS.register("vis_conduit",
        () -> new VisPipeBlock(BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().sound(SoundType.WOOD), false));
    public static final DeferredBlock<VisPipeBlock> FILTER = ThaumicAdditions.BLOCKS.register("filter",
        () -> new VisPipeBlock(BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.WOOD), true));

    public static final DeferredBlock<InfuserBlock> INFUSER = ThaumicAdditions.BLOCKS.register("infuser",
        () -> new InfuserBlock(BlockBehaviour.Properties.of().strength(0, 1.5F).noOcclusion().sound(SoundType.METAL), false));
    public static final DeferredBlock<InfuserBlock> DARK_INFUSER = ThaumicAdditions.BLOCKS.register("dark_infuser",
        () -> new InfuserBlock(BlockBehaviour.Properties.of().strength(0, 1.5F).noOcclusion().sound(SoundType.METAL), true));

    public static final DeferredBlock<thaumicadditions.block.CondenserBlock> OLD_CONDENSATOR = ThaumicAdditions.BLOCKS.register("old_condensator",
        () -> new thaumicadditions.block.CondenserBlock(BlockBehaviour.Properties.of().strength(1).noOcclusion().sound(SoundType.WOOD)));

    public static final DeferredBlock<thaumicadditions.block.CrystallizerBlock> THAUMIC_CRYSTALLIZER = ThaumicAdditions.BLOCKS.register("thaumic_crystallizer",
        () -> new thaumicadditions.block.CrystallizerBlock(BlockBehaviour.Properties.of().strength(1.5F, 3).noOcclusion().sound(SoundType.METAL)));

    public static final DeferredBlock<thaumicadditions.block.ThaumicGeneratorBlock> THAUMIC_GENERATOR = ThaumicAdditions.BLOCKS.register("thaumic_generator",
        () -> new thaumicadditions.block.ThaumicGeneratorBlock(BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().sound(SoundType.GLASS)));

    public static final DeferredBlock<thaumicadditions.block.DarkGeneratorBlock> DARK_GENERATOR = ThaumicAdditions.BLOCKS.register("dark_generator",
        () -> new thaumicadditions.block.DarkGeneratorBlock(BlockBehaviour.Properties.of().strength(1, 3).noOcclusion().sound(SoundType.METAL)));

    public static final DeferredBlock<thaumicadditions.block.PureWarpBlock> PURE_WARP = ThaumicAdditions.BLOCKS.register("pure_warp",
        () -> new thaumicadditions.block.PureWarpBlock(BlockBehaviour.Properties.of().replaceable().noCollission().strength(100)
            .pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY)));

    public static final DeferredBlock<thaumicadditions.block.RestorerBlock> THAUMIC_RESTORER = ThaumicAdditions.BLOCKS.register("thaumic_restorer",
        () -> new thaumicadditions.block.RestorerBlock(BlockBehaviour.Properties.of().strength(1, 3).noOcclusion().sound(ModSounds.WOOD_GLASS)));

    public static final DeferredBlock<thaumicadditions.block.InfusionActivatorBlock> INFUSION_ACTIVATOR = ThaumicAdditions.BLOCKS.register("infuser_activator",
        () -> new thaumicadditions.block.InfusionActivatorBlock(BlockBehaviour.Properties.of().strength(5, 2).noOcclusion().sound(SoundType.STONE)));

    public static final DeferredBlock<thaumicadditions.block.TileBoosterBlock> TILE_BOOSTER = ThaumicAdditions.BLOCKS.register("tile_booster",
        () -> new thaumicadditions.block.TileBoosterBlock(BlockBehaviour.Properties.of().strength(1).noOcclusion().sound(SoundType.STONE)));

    public static final DeferredBlock<thaumicadditions.block.CreativeNodeBlock> CREATIVE_NODE = ThaumicAdditions.BLOCKS.register("creative_node",
        () -> new thaumicadditions.block.CreativeNodeBlock(BlockBehaviour.Properties.of().noCollission().noOcclusion().sound(SoundType.EMPTY)));

    public static final DeferredBlock<thaumicadditions.block.HyperNodeCreatorBlock> HYPER_NODE_CREATOR = ThaumicAdditions.BLOCKS.register("hyper_node_creator",
        () -> new thaumicadditions.block.HyperNodeCreatorBlock(BlockBehaviour.Properties.of().strength(-1, 0).noOcclusion().sound(SoundType.METAL)));

    public static final DeferredBlock<thaumicadditions.block.EldritchStoneBlock> ELDRITCH_STONE = ThaumicAdditions.BLOCKS.register("eldritch_stone",
        () -> new thaumicadditions.block.EldritchStoneBlock(BlockBehaviour.Properties.of().strength(1, 5).sound(SoundType.STONE)));
    public static final DeferredBlock<thaumicadditions.block.EldritchStoneBlock> UNBREAKABLE_ELDRITCH_STONE = ThaumicAdditions.BLOCKS.register("unbreakable_eldritch_stone",
        () -> new thaumicadditions.block.EldritchStoneBlock(BlockBehaviour.Properties.of().strength(-1, 3600000).noLootTable().sound(SoundType.STONE)));
    public static final DeferredBlock<thaumicadditions.block.EldritchLockBlock> ELDRITCH_LOCK = ThaumicAdditions.BLOCKS.register("eldritch_lock",
        () -> new thaumicadditions.block.EldritchLockBlock(BlockBehaviour.Properties.of().strength(-1, 3600000).noLootTable().sound(SoundType.STONE)));
    public static final DeferredBlock<thaumicadditions.block.VoidChestBlock> VOID_CHEST = ThaumicAdditions.BLOCKS.register("void_chest",
        () -> new thaumicadditions.block.VoidChestBlock(BlockBehaviour.Properties.of().strength(1).noOcclusion().sound(SoundType.STONE)));
    public static final DeferredBlock<thaumicadditions.block.ReturnerBlock> RETURNER = ThaumicAdditions.BLOCKS.register("returner",
        () -> new thaumicadditions.block.ReturnerBlock(BlockBehaviour.Properties.of().strength(-1, 3600000).noCollission().noOcclusion().noLootTable()));

    public static final DeferredBlock<thaumicadditions.block.SealBlock> SEAL = ThaumicAdditions.BLOCKS.register("arcane_seal",
        () -> new thaumicadditions.block.SealBlock(BlockBehaviour.Properties.of().strength(0).noCollission().noOcclusion().sound(SoundType.STONE)));
    public static final DeferredBlock<thaumicadditions.block.SymbollerBlock> SYMBOLLER = ThaumicAdditions.BLOCKS.register("symboller",
        () -> new thaumicadditions.block.SymbollerBlock(BlockBehaviour.Properties.of().strength(3, 0.5F).noOcclusion().sound(SoundType.STONE)));

    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> ELDRITCH_STAIRS = ThaumicAdditions.BLOCKS.register("stairs_eldritch",
        () -> new net.minecraft.world.level.block.StairBlock(ELDRITCH_STONE.get().defaultBlockState(),
            BlockBehaviour.Properties.of().strength(1, 5).sound(SoundType.STONE)));

    private ModBlocks() {
    }

    public static void init() {
    }
}
