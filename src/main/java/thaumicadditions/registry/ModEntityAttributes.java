package thaumicadditions.registry;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import thaumicadditions.ThaumicAdditions;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class ModEntityAttributes {
    private ModEntityAttributes() {
    }

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CARPET.get(), LivingEntity.createLivingAttributes().build());
    }
}
