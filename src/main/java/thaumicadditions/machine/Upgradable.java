package thaumicadditions.machine;

import net.minecraft.world.item.ItemStack;

public interface Upgradable {
    boolean canInstall(UpgradeType type);

    boolean installUpgrade(ItemStack stack);

    ItemStack removeUpgrade();
}
