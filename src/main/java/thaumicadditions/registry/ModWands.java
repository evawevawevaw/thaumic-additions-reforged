package thaumicadditions.registry;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.wand.WandCap;
import thaumcraft.wand.WandRegistry;
import thaumcraft.wand.WandRod;
import thaumicadditions.ThaumicAdditions;

public final class ModWands {
    public static final WandCap ADAMINITE_CAP = WandRegistry.registerCap(
        "ADAMINITE", 0.5F, List.of(), 0.0F, item("cap_adaminite"), 15, null,
        item("textures/model/wand_cap_adaminite.png")
    );
    public static final WandCap MITHRILLIUM_CAP = WandRegistry.registerCap(
        "MITHRILLIUM", 0.75F, List.of(), 0.0F, item("cap_mithrillium"), 10, null,
        item("textures/model/wand_cap_mithrillium.png")
    );
    public static final WandRod ADAMINITEWOOD_ROD = WandRegistry.registerRod(
        "ADAMINITEWOOD", 2000, item("wand_rod_adaminitewood"), 50, null, false, false, false,
        item("textures/model/wand_rod_adaminitewood.png")
    );
    public static final WandRod ADAMINITEWOOD_STAFF = WandRegistry.registerRod(
        "ADAMINITEWOOD", 4500, item("wand_staff_adaminitewood"), 125, null, true, true, true,
        item("textures/model/wand_rod_adaminitewood.png")
    );

    private ModWands() {
    }

    public static void init() {
    }

    private static ResourceLocation item(String path) {
        return ResourceLocation.fromNamespaceAndPath(ThaumicAdditions.MOD_ID, path);
    }
}
