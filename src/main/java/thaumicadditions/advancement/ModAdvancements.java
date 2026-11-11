package thaumicadditions.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import thaumicadditions.ThaumicAdditions;

public final class ModAdvancements {
    private ModAdvancements() {
    }

    public static void award(ServerPlayer player, String path) {
        AdvancementHolder advancement = player.serverLevel().getServer().getAdvancements()
            .get(ResourceLocation.fromNamespaceAndPath(ThaumicAdditions.MOD_ID, path));
        if (advancement == null) {
            return;
        }
        AdvancementHolder root = player.serverLevel().getServer().getAdvancements()
            .get(ResourceLocation.fromNamespaceAndPath(ThaumicAdditions.MOD_ID, "root"));
        if (root != null) {
            player.getAdvancements().award(root, "trigger");
        }
        player.getAdvancements().award(advancement, "trigger");
    }
}
