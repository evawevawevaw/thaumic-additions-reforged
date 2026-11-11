package thaumicadditions.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.item.AdaminiteArmorItem;
import thaumicadditions.item.AdaminiteIngotItem;
import thaumicadditions.item.WarpingBlockItem;
import thaumicadditions.item.InfusionBookItem;
import thaumicadditions.item.AdaminiteSwordItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThaumicAdditions.MOD_ID);
    public static final DeferredItem<AdaminiteIngotItem> ADAMINITE_INGOT = ITEMS.registerItem("adaminite_ingot", AdaminiteIngotItem::new);
    public static final DeferredItem<thaumicadditions.item.MaskOfCrueltyItem> MASK_OF_CRUELTY =
        ITEMS.registerItem("mask_of_cruelty", thaumicadditions.item.MaskOfCrueltyItem::new);
    public static final DeferredItem<thaumicadditions.item.ProjectileFocusItem> FOCI_PROJECTILE =
        ITEMS.registerItem("foci_projectile", thaumicadditions.item.ProjectileFocusItem::new);
    public static final DeferredItem<thaumicadditions.item.ExperienceFocusItem> FOCI_XP =
        ITEMS.registerItem("foci_xp", thaumicadditions.item.ExperienceFocusItem::new);
    public static final DeferredItem<Item> MITHRILLIUM_INGOT = ITEMS.registerSimpleItem("mithrillium_ingot");
    public static final DeferredItem<Item> CAP_ADAMINITE = ITEMS.registerSimpleItem("cap_adaminite");
    public static final DeferredItem<Item> CAP_ADAMINITE_INERT = ITEMS.registerSimpleItem("cap_adaminite_inert");
    public static final DeferredItem<Item> CAP_MITHRILLIUM = ITEMS.registerSimpleItem("cap_mithrillium");
    public static final DeferredItem<Item> CAP_MITHRILLIUM_INERT = ITEMS.registerSimpleItem("cap_mithrillium_inert");
    public static final DeferredItem<Item> WAND_ROD_ADAMINITEWOOD = ITEMS.registerSimpleItem("wand_rod_adaminitewood");
    public static final DeferredItem<Item> WAND_STAFF_ADAMINITEWOOD = ITEMS.registerSimpleItem("wand_staff_adaminitewood");
    public static final DeferredItem<Item> SINGCORE = ITEMS.registerSimpleItem("singcore");
    public static final DeferredItem<Item> DEPLETED_SHARD = ITEMS.registerSimpleItem("depleted_shard");
    public static final DeferredItem<Item> TAINTED_ROOTS = ITEMS.registerSimpleItem("tainted_roots");
    public static final DeferredItem<Item> SOUL_FRAGMENT = ITEMS.registerSimpleItem("soul_fragment");
    public static final DeferredItem<Item> ENCHANTED_GREATWOOD = ITEMS.registerSimpleItem("enchanted_greatwood", new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final DeferredItem<Item> ENCHANTED_SILVERWOOD = ITEMS.registerSimpleItem("enchanted_silverwood", new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));

    public static final DeferredItem<AdaminiteSwordItem> ADAMINITE_SWORD =
        ITEMS.registerItem("adaminite_sword", AdaminiteSwordItem::new);
    public static final DeferredItem<AdaminiteArmorItem> ADAMINITE_HELMET =
        ITEMS.registerItem("adaminite_helmet", properties -> new AdaminiteArmorItem(ArmorItem.Type.HELMET, properties));
    public static final DeferredItem<AdaminiteArmorItem> ADAMINITE_CHESTPLATE =
        ITEMS.registerItem("adaminite_chestplate", properties -> new AdaminiteArmorItem(ArmorItem.Type.CHESTPLATE, properties));
    public static final DeferredItem<AdaminiteArmorItem> ADAMINITE_LEGGINGS =
        ITEMS.registerItem("adaminite_leggings", properties -> new AdaminiteArmorItem(ArmorItem.Type.LEGGINGS, properties));
    public static final DeferredItem<AdaminiteArmorItem> ADAMINITE_BOOTS =
        ITEMS.registerItem("adaminite_boots", properties -> new AdaminiteArmorItem(ArmorItem.Type.BOOTS, properties));

    public static final DeferredItem<ItemNameBlockItem> TAINTED_SEED = ITEMS.register("tainted_seed",
        () -> new ItemNameBlockItem(ModBlocks.TAINTED_CROPS.get(), new Item.Properties()));

    public static final DeferredItem<thaumicadditions.item.SealItem> SEAL_ITEM = ITEMS.register("seal_item",
        () -> new thaumicadditions.item.SealItem(ModBlocks.SEAL.get(), new Item.Properties()));
    public static final DeferredItem<thaumicadditions.item.SealSymbolItem> SEAL_SYMBOL =
        ITEMS.registerItem("seal_symbol", thaumicadditions.item.SealSymbolItem::new);
    public static final DeferredItem<thaumicadditions.item.SealGlobeItem> SEAL_GLOBE =
        ITEMS.registerItem("seal_globe", thaumicadditions.item.SealGlobeItem::new);
    public static final DeferredItem<BlockItem> SYMBOLLER = ITEMS.registerSimpleBlockItem(ModBlocks.SYMBOLLER);
    public static final DeferredItem<BlockItem> ELDRITCH_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.ELDRITCH_STAIRS);
    public static final DeferredItem<BlockItem> ELDRITCH_STONE = ITEMS.registerSimpleBlockItem(ModBlocks.ELDRITCH_STONE);
    public static final DeferredItem<BlockItem> UNBREAKABLE_ELDRITCH_STONE = ITEMS.registerSimpleBlockItem(ModBlocks.UNBREAKABLE_ELDRITCH_STONE);
    public static final DeferredItem<BlockItem> ELDRITCH_LOCK = ITEMS.registerSimpleBlockItem(ModBlocks.ELDRITCH_LOCK);
    public static final DeferredItem<BlockItem> VOID_CHEST = ITEMS.registerSimpleBlockItem(ModBlocks.VOID_CHEST);
    public static final DeferredItem<BlockItem> RETURNER = ITEMS.registerSimpleBlockItem(ModBlocks.RETURNER);
    public static final DeferredItem<BlockItem> TANK_BASIC = ITEMS.registerSimpleBlockItem(ModBlocks.TANK_BASIC);
    public static final DeferredItem<BlockItem> TANK_REINFORCED = ITEMS.registerSimpleBlockItem(ModBlocks.TANK_REINFORCED);
    public static final DeferredItem<BlockItem> OLD_CRUCIBLE = ITEMS.registerSimpleBlockItem(ModBlocks.OLD_CRUCIBLE);

    public static final DeferredItem<BlockItem> VIS_CONDUIT = ITEMS.registerSimpleBlockItem(ModBlocks.VIS_CONDUIT);
    public static final DeferredItem<BlockItem> FILTER = ITEMS.registerSimpleBlockItem(ModBlocks.FILTER);

    public static final DeferredItem<BlockItem> INFUSER = ITEMS.registerSimpleBlockItem(ModBlocks.INFUSER);
    public static final DeferredItem<WarpingBlockItem> DARK_INFUSER = ITEMS.registerItem("dark_infuser",
        properties -> new WarpingBlockItem(ModBlocks.DARK_INFUSER.get(), properties, 3));

    public static final DeferredItem<InfusionBookItem> INFUSER_BOOK = ITEMS.registerItem("infuser_book",
        properties -> new InfusionBookItem(properties, false));
    public static final DeferredItem<InfusionBookItem> DARK_INFUSER_BOOK = ITEMS.registerItem("dark_infuser_book",
        properties -> new InfusionBookItem(properties, true));
    public static final DeferredItem<Item> INERT_KEYSTONE = ITEMS.registerSimpleItem("inert_keystone", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ELDRITCH_KEYSTONE_TLHUTLH = ITEMS.registerSimpleItem("eldritch_keystone_tlhutlh", new Item.Properties().stacksTo(1));

    public static final DeferredItem<BlockItem> OLD_CONDENSATOR = ITEMS.registerSimpleBlockItem(ModBlocks.OLD_CONDENSATOR);
    public static final DeferredItem<thaumicadditions.item.UpgradeItem> UPGRADE_SPEED = ITEMS.registerItem("upgrade_speed",
        properties -> new thaumicadditions.item.UpgradeItem(properties, thaumicadditions.machine.UpgradeType.SPEED));
    public static final DeferredItem<thaumicadditions.item.UpgradeItem> UPGRADE_EFFICIENCY = ITEMS.registerItem("upgrade_efficiency",
        properties -> new thaumicadditions.item.UpgradeItem(properties, thaumicadditions.machine.UpgradeType.EFFICIENCY));
    public static final DeferredItem<thaumicadditions.item.UpgradeItem> UPGRADE_ANGER = ITEMS.registerItem("upgrade_anger",
        properties -> new thaumicadditions.item.UpgradeItem(properties, thaumicadditions.machine.UpgradeType.ANGER));
    public static final DeferredItem<thaumicadditions.item.UpgradeItem> UPGRADE_TAINT = ITEMS.registerItem("upgrade_taint",
        properties -> new thaumicadditions.item.UpgradeItem(properties, thaumicadditions.machine.UpgradeType.TAINT));
    public static final DeferredItem<thaumicadditions.item.UpgradeItem> UPGRADE_SADNESS = ITEMS.registerItem("upgrade_sadness",
        properties -> new thaumicadditions.item.UpgradeItem(properties, thaumicadditions.machine.UpgradeType.SADNESS));
    public static final DeferredItem<thaumicadditions.item.UpgradeItem> UPGRADE_CAPACITY = ITEMS.registerItem("upgrade_capacity",
        properties -> new thaumicadditions.item.UpgradeItem(properties, thaumicadditions.machine.UpgradeType.CAPACITY));
    public static final DeferredItem<thaumicadditions.item.UpgradeItem> UPGRADE_KNOWLEDGE = ITEMS.registerItem("upgrade_knowledge",
        properties -> new thaumicadditions.item.UpgradeItem(properties, thaumicadditions.machine.UpgradeType.KNOWLEDGE));

    public static final DeferredItem<BlockItem> THAUMIC_CRYSTALLIZER = ITEMS.registerSimpleBlockItem(ModBlocks.THAUMIC_CRYSTALLIZER);

    public static final DeferredItem<BlockItem> THAUMIC_GENERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.THAUMIC_GENERATOR);

    public static final DeferredItem<BlockItem> DARK_GENERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.DARK_GENERATOR);

    public static final DeferredItem<net.minecraft.world.item.BucketItem> PURE_WARP_BUCKET = ITEMS.registerItem("pure_warp_bucket",
        properties -> new net.minecraft.world.item.BucketItem(ModFluids.PURE_WARP.get(), properties.stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET)));

    public static final DeferredItem<BlockItem> THAUMIC_RESTORER = ITEMS.registerSimpleBlockItem(ModBlocks.THAUMIC_RESTORER);

    public static final DeferredItem<BlockItem> INFUSION_ACTIVATOR = ITEMS.registerSimpleBlockItem(ModBlocks.INFUSION_ACTIVATOR);

    public static final DeferredItem<BlockItem> TILE_BOOSTER = ITEMS.registerSimpleBlockItem(ModBlocks.TILE_BOOSTER);

    public static final DeferredItem<BlockItem> CREATIVE_NODE = ITEMS.registerSimpleBlockItem(ModBlocks.CREATIVE_NODE);

    public static final DeferredItem<BlockItem> HYPER_NODE_CREATOR = ITEMS.registerSimpleBlockItem(ModBlocks.HYPER_NODE_CREATOR);

    public static final DeferredItem<thaumicadditions.item.ArcaneStoneItem> ARCANE_STONE = ITEMS.registerItem("arcane_stone", thaumicadditions.item.ArcaneStoneItem::new);

    public static final DeferredItem<thaumicadditions.item.ArcaneTransporterItem> ARCANE_TRANSPORTER = ITEMS.registerItem("arcane_transporter", thaumicadditions.item.ArcaneTransporterItem::new);

    public static final DeferredItem<thaumicadditions.item.ArcaneDiceItem> ARCANE_DICE = ITEMS.registerItem("arcane_dice", thaumicadditions.item.ArcaneDiceItem::new);
    public static final DeferredItem<thaumicadditions.item.HallelujahWandItem> HALLELUJAH_WAND = ITEMS.registerItem("wand_hallelujah", thaumicadditions.item.HallelujahWandItem::new);

    public static final DeferredItem<thaumicadditions.item.SingularityItem> SINGULARITY = ITEMS.registerItem("singularity", thaumicadditions.item.SingularityItem::new);

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
