package thaumicadditions.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.item.AdaminiteArmorItem;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class AdaminiteArmorEvents {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private AdaminiteArmorEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof AdaminiteArmorItem armor && armor.getEquipmentSlot() == slot) {
                armor.tickEquipped(stack, player);
            }
        }
    }
}
