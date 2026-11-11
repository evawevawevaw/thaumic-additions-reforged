package thaumicadditions.registry;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.focus.FocusUpgradeType;

public final class ModFocusUpgrades {
    public static final FocusUpgradeType INFECTION = register("infection", Aspect.TAINT, 1);
    public static final FocusUpgradeType PURITY = register("purity", Aspect.MAGIC, 2);
    public static final FocusUpgradeType EXPLOSIVE = register("explosive", Aspect.VOID, 2);

    private ModFocusUpgrades() {
    }

    public static void init() {
    }

    private static FocusUpgradeType register(String name, Aspect aspect, int amount) {
        int id = 0;
        while (FocusUpgradeType.byId(id) != null) {
            id++;
        }
        return new FocusUpgradeType(id, "thaumicadditions:" + name,
            ResourceLocation.parse("thaumicadditions:textures/foci/" + name + ".png"), new AspectList().add(aspect, amount));
    }
}
