package thaumicadditions.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.SimpleTier;
import thaumicadditions.registry.ModDataComponents;

public final class AdaminiteSwordItem extends SwordItem {
    private static final Tier MATERIAL = new SimpleTier(
        BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 46357, 10.0F, 23.0F, 26, () -> Ingredient.EMPTY);
    private static final double AURA_RANGE = 10.0;

    public AdaminiteSwordItem(Properties properties) {
        super(MATERIAL, properties.attributes(SwordItem.createAttributes(MATERIAL, 3.0F, -2.4F)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND || !player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide) {
            SwordAuraMode mode = mode(stack).next();
            stack.set(ModDataComponents.SWORD_AURA_MODE.get(), mode.ordinal());
            resetCharge(stack);
            player.displayClientMessage(Component.translatable("tooltip.thaumicadditions.sword_aura_mode", mode.damage()), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity owner, int slot, boolean selected) {
        if (level.isClientSide) {
            return;
        }
        if (!(owner instanceof Player player) || !selected || player.getMainHandItem() != stack
            || !player.isAlive() || player.isSpectator() || !player.isShiftKeyDown()) {
            resetCharge(stack);
            return;
        }
        AABB area = new AABB(player.getX() - AURA_RANGE, player.getY() - AURA_RANGE, player.getZ() - AURA_RANGE,
            player.getX() + AURA_RANGE, player.getY() + AURA_RANGE, player.getZ() + AURA_RANGE);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
            target -> target != player && target.isAlive() && !target.isSpectator()
                && (!(target instanceof Player other) || player.canHarmPlayer(other)));
        if (targets.isEmpty()) {
            resetCharge(stack);
            return;
        }
        SwordAuraMode mode = mode(stack);
        int ticks = stack.getOrDefault(ModDataComponents.SWORD_AURA_TICKS.get(), 0) + 1;
        if (ticks < mode.interval()) {
            stack.set(ModDataComponents.SWORD_AURA_TICKS.get(), ticks);
            return;
        }
        resetCharge(stack);
        for (LivingEntity target : targets) {
            target.hurt(level.damageSources().indirectMagic(player, player), mode.damage());
        }
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        SwordAuraMode mode = mode(stack);
        tooltip.add(Component.translatable("tooltip.thaumicadditions.sword_aura_mode", mode.damage())
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.thaumicadditions.sword_aura_interval", mode.interval())
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.thaumicadditions.sword_aura_controls")
            .withStyle(ChatFormatting.DARK_GRAY));
    }

    private static SwordAuraMode mode(ItemStack stack) {
        return SwordAuraMode.fromIndex(stack.getOrDefault(ModDataComponents.SWORD_AURA_MODE.get(), 0));
    }

    private static void resetCharge(ItemStack stack) {
        stack.remove(ModDataComponents.SWORD_AURA_TICKS.get());
    }
}
