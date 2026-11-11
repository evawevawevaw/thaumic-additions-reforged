package thaumicadditions.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thaumcraft.aspect.Aspect;
import thaumcraft.item.gear.Goggles;
import thaumcraft.item.gear.WarpingGear;
import thaumcraft.node.Revealer;
import thaumcraft.vis.VisNet;
import thaumcraft.wand.VisDiscountGear;
import thaumicadditions.registry.ModArmorMaterials;
import thaumicadditions.registry.ModDataComponents;

public final class AdaminiteArmorItem extends ArmorItem implements WarpingGear, VisDiscountGear, Goggles, Revealer {
    public static final int MAX_CHARGE = 80000;

    public AdaminiteArmorItem(Type type, Properties properties) {
        super(ModArmorMaterials.ADAMINITE, type,
            properties.durability(type.getDurability(ModArmorMaterials.DURABILITY_MULTIPLIER)));
    }

    public void tickEquipped(ItemStack stack, Player player) {
        if (player.level().isClientSide || player.isSpectator() || !player.isAlive()
            || player.getItemBySlot(getEquipmentSlot()) != stack) {
            return;
        }
        int charge = charge(stack);
        int drained = VisNet.drainVis(player.level(), player.blockPosition(), chargingAspect(), 1);
        int updated = drained > 0 ? Math.min(MAX_CHARGE, charge + 1) : Math.max(0, charge - 1);
        if (updated != charge) {
            if (updated == 0) {
                stack.remove(ModDataComponents.ARMOR_VIS_CHARGE.get());
            } else {
                stack.set(ModDataComponents.ARMOR_VIS_CHARGE.get(), updated);
            }
        }
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player, Aspect aspect) {
        if (charge(stack) == 0) {
            return 0;
        }
        return switch (getType()) {
            case HELMET -> aspect == Aspect.FIRE ? 10 : 7;
            case CHESTPLATE -> aspect == Aspect.FIRE ? 14 : 9;
            case LEGGINGS -> aspect == Aspect.FIRE ? 10 : 8;
            case BOOTS -> aspect == Aspect.FIRE ? 7 : 6;
            default -> 0;
        };
    }

    @Override
    public boolean showNodes(ItemStack stack, LivingEntity entity) {
        return getType() == Type.HELMET && charge(stack) > 0;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, LivingEntity entity) {
        return showNodes(stack, entity);
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
        ArmorMaterial.Layer layer, boolean innerModel) {
        if (charge(stack) == 0) {
            return null;
        }
        int frame = 1 + (entity.tickCount % 11) * 3 / 10;
        int textureLayer = innerModel ? 2 : 1;
        return ResourceLocation.parse("thaumicadditions:textures/models/armor/adaminite_" + textureLayer + "/" + frame + ".png");
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 5;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.thaumicadditions.armor_charge", charge(stack), MAX_CHARGE)
            .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.thaumicadditions.armor_charging_aspect", chargingAspect().getDescription())
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.thaumicadditions.armor_vis_discount", getVisDiscount(stack, null, null))
            .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.thaumicadditions.armor_fire_discount", getVisDiscount(stack, null, Aspect.FIRE))
            .withStyle(ChatFormatting.DARK_PURPLE));
    }

    private Aspect chargingAspect() {
        return switch (getType()) {
            case HELMET -> Aspect.AIR;
            case CHESTPLATE -> Aspect.EARTH;
            case LEGGINGS -> Aspect.WATER;
            case BOOTS -> Aspect.FIRE;
            default -> throw new IllegalStateException("Unsupported adaminite armor type: " + getType());
        };
    }

    private static int charge(ItemStack stack) {
        return Math.clamp(stack.getOrDefault(ModDataComponents.ARMOR_VIS_CHARGE.get(), 0), 0, MAX_CHARGE);
    }
}
