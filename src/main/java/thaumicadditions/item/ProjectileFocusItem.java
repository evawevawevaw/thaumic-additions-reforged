package thaumicadditions.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.entity.CombatUtils;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.Wands;
import thaumcraft.wand.focus.FocusUpgradeType;
import thaumcraft.wand.focus.PrimalFocus;
import thaumicadditions.entity.FocusProjectile;
import thaumicadditions.registry.ModEntities;
import thaumicadditions.registry.ModFocusUpgrades;

public final class ProjectileFocusItem extends AddonFocusItem {
    public ProjectileFocusItem(Properties properties) {
        super(properties, "foci_projectile");
    }

    @Override
    public AspectList getVisCost(ItemStack focus) {
        AspectList cost = new AspectList();
        int amount = 200 / (getUpgradeLevel(focus, FocusUpgradeType.FRUGAL) + 1);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            cost.add(aspect, amount);
        }
        return cost;
    }

    @Override
    public int getActivationCooldown(ItemStack focus) {
        return 500;
    }

    @Override
    public InteractionResult onFocusRightClick(ItemStack wand, Level level, Player player, HitResult hit) {
        ItemStack focus = Wands.getFocusItem(wand);
        if (level instanceof ServerLevel server && wand.getItem() instanceof WandItem item
            && item.consumeAllVis(wand, player, getVisCost(focus), true, false)) {
            FocusProjectile projectile = new FocusProjectile(ModEntities.FOCUS_PROJECTILE.get(), server);
            projectile.configure(isUpgradedWith(focus, ModFocusUpgrades.INFECTION),
                isUpgradedWith(focus, ModFocusUpgrades.PURITY), getUpgradeLevel(focus, ModFocusUpgrades.EXPLOSIVE));
            projectile.setSeeker(isUpgradedWith(focus, PrimalFocus.SEEKER));
            CombatUtils.launch(projectile, player, 0.5F);
            server.addFreshEntity(projectile);
            server.playSound(null, projectile.blockPosition(), thaumcraft.registry.ModSounds.ICE.get(), SoundSource.PLAYERS, 1, 1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canApplyUpgrade(ItemStack focus, Player player, FocusUpgradeType type, int rank) {
        return type != ModFocusUpgrades.INFECTION && type != ModFocusUpgrades.PURITY
            || !isUpgradedWith(focus, ModFocusUpgrades.INFECTION) && !isUpgradedWith(focus, ModFocusUpgrades.PURITY);
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focus, int rank) {
        return rank == 3 ? new FocusUpgradeType[]{FocusUpgradeType.FRUGAL, ModFocusUpgrades.INFECTION,
            ModFocusUpgrades.PURITY, ModFocusUpgrades.EXPLOSIVE, PrimalFocus.SEEKER}
            : new FocusUpgradeType[]{FocusUpgradeType.FRUGAL, ModFocusUpgrades.INFECTION,
                ModFocusUpgrades.PURITY, ModFocusUpgrades.EXPLOSIVE};
    }
}
