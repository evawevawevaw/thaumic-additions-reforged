package thaumicadditions.registry;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumicadditions.ThaumicAdditions;

public final class ModArmorMaterials {
    public static final int DURABILITY_MULTIPLIER = 46357;
    private static final DeferredRegister<ArmorMaterial> MATERIALS =
        DeferredRegister.create(Registries.ARMOR_MATERIAL, ThaumicAdditions.MOD_ID);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ADAMINITE =
        MATERIALS.register("adaminite", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 5, ArmorItem.Type.CHESTPLATE, 10,
                ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 4, ArmorItem.Type.BODY, 10),
            26, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.parse("thaumicadditions:adaminite"))),
            0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MASK_OF_CRUELTY =
        MATERIALS.register("mask_of_cruelty", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 0), 100, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.parse("thaumicadditions:mask_of_cruelty"))), 0, 0));

    private ModArmorMaterials() {
    }

    public static void register(IEventBus modEventBus) {
        MATERIALS.register(modEventBus);
    }
}
