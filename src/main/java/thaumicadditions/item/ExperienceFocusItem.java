package thaumicadditions.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.Wands;
import thaumcraft.wand.focus.FocusUpgradeType;
import thaumicadditions.registry.ModFocusUpgrades;

public final class ExperienceFocusItem extends AddonFocusItem {
    public ExperienceFocusItem(Properties properties) {
        super(properties, "foci_xp");
    }

    @Override
    public AspectList getVisCost(ItemStack focus) {
        AspectList cost = new AspectList();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            cost.add(aspect, 1000);
        }
        return cost;
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focus) {
        return true;
    }

    @Override
    public InteractionResult onFocusRightClick(ItemStack wand, Level level, Player player, HitResult hit) {
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUsingFocusTick(ItemStack wand, Player player, int count) {
        if (player.level().isClientSide || !(wand.getItem() instanceof WandItem item)) {
            return;
        }
        ItemStack focus = Wands.getFocusItem(wand);
        if (isUpgradedWith(focus, ModFocusUpgrades.INFECTION)) {
            if (player.experienceLevel < 1) {
                return;
            }
            boolean room = false;
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                room |= Wands.getVis(wand, aspect) < Wands.getMaxVis(wand);
            }
            if (room) {
                player.giveExperienceLevels(-1);
                for (Aspect aspect : Aspect.getPrimalAspects()) {
                    Wands.addRealVis(wand, aspect, 100, true);
                }
            }
        } else if (item.consumeAllVis(wand, player, getVisCost(focus), true, false)) {
            player.giveExperienceLevels(1);
        }
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focus, int rank) {
        return isUpgradedWith(focus, ModFocusUpgrades.INFECTION) ? new FocusUpgradeType[0]
            : new FocusUpgradeType[]{ModFocusUpgrades.INFECTION};
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        boolean inverted = isUpgradedWith(stack, ModFocusUpgrades.INFECTION);
        tooltip.add(Component.translatable(inverted ? "tooltip.thaumicadditions.xp_drain" : "tooltip.thaumicadditions.xp_gain")
            .withStyle(ChatFormatting.GREEN));
        if (inverted) {
            tooltip.add(Component.translatable("tooltip.thaumicadditions.xp_vis_return").withStyle(ChatFormatting.GRAY));
            addFocusInformation(stack, tooltip);
        } else {
            super.appendHoverText(stack, context, tooltip, flag);
        }
    }
}
