package thaumicadditions.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thaumcraft.entity.CombatUtils;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.entity.CrueltyProbe;
import thaumicadditions.registry.ModEntities;
import thaumicadditions.registry.ModItems;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class CrueltyMaskEvents {
    private CrueltyMaskEvents() {
    }

    @SubscribeEvent
    public static void tick(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (player.level().isClientSide || !player.isAlive() || player.isSpectator()
            || !player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.MASK_OF_CRUELTY.get())) {
            return;
        }
        CrueltyProbe probe = new CrueltyProbe(ModEntities.CRUELTY_PROBE.get(), player.level());
        CombatUtils.launch(probe, player, 1.5F, 1);
        player.level().addFreshEntity(probe);
    }
}
