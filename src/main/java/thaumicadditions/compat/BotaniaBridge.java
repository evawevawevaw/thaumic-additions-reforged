package thaumicadditions.compat;

import com.mojang.logging.LogUtils;
import java.lang.reflect.Method;
import java.util.function.IntUnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public final class BotaniaBridge {
    private static boolean initialized;
    private static boolean disabled;
    private static Object lookup;
    private static Method find;
    private static Method current;
    private static Method full;
    private static Method receive;

    private BotaniaBridge() {
    }

    private static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!ModList.get().isLoaded("botania")) {
            disabled = true;
            return;
        }
        try {
            Class<?> api = Class.forName("vazkii.botania.api.mana.ManaReceiver");
            lookup = api.getField("LOOKUP").get(null);
            find = lookup.getClass().getMethod("find", Level.class, BlockPos.class, Object.class);
            current = api.getMethod("getCurrentMana");
            full = api.getMethod("isFull");
            receive = api.getMethod("receiveMana", int.class);
        } catch (ReflectiveOperationException | LinkageError failure) {
            disable(failure);
        }
    }

    private static void disable(Throwable failure) {
        if (!disabled) {
            disabled = true;
            LogUtils.getLogger().warn("Botania mana integration is unavailable for the installed API", failure);
        }
    }

    public static IntUnaryOperator receiver(Level level, BlockPos pos, Direction side) {
        init();
        if (disabled) {
            return null;
        }
        try {
            Object receiver = find.invoke(lookup, level, pos, side);
            if (receiver == null || Boolean.TRUE.equals(full.invoke(receiver))) {
                return null;
            }
            return amount -> accept(receiver, amount);
        } catch (ReflectiveOperationException | LinkageError failure) {
            disable(failure);
            return null;
        }
    }

    private static int accept(Object receiver, int amount) {
        boolean attempted = false;
        try {
            int before = (Integer)current.invoke(receiver);
            attempted = true;
            receive.invoke(receiver, amount);
            int after = (Integer)current.invoke(receiver);
            return (int)Math.clamp((long)after - before, 0, amount);
        } catch (ReflectiveOperationException | LinkageError failure) {
            disable(failure);
            return attempted ? amount : 0;
        }
    }
}
