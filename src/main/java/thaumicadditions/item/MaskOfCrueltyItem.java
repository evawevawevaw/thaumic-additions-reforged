package thaumicadditions.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import thaumicadditions.registry.ModArmorMaterials;

public final class MaskOfCrueltyItem extends ArmorItem {
    public MaskOfCrueltyItem(Properties properties) {
        super(ModArmorMaterials.MASK_OF_CRUELTY, Type.HELMET, properties.durability(Type.HELMET.getDurability(100)));
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
        ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.parse("thaumicadditions:textures/model/maskofcruelty.png");
    }
}
