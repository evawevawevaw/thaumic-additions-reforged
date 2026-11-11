package thaumicadditions.event;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.magic.PipeNetwork;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID)
public final class PipeLifecycle {
    private PipeLifecycle() {
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            PipeNetwork.invalidate(level);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PipeNetwork.clearAll();
    }
}
