package thaumicadditions.seal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import thaumicadditions.blockentity.SealBlockEntity;

public final class SealBehaviors {
    private static final Map<List<String>, Effect> EFFECTS = new LinkedHashMap<>();
    private static final Effect NONE = new Effect(List.of("seal.noeffect.globe"), seal -> {}, (seal, player) -> false);

    static {
        register(List.of("praecantatio", "instrumentum", "permutatio"), List.of("seal.repair.globe"), SealActions::repair);
        register(List.of("vitium", "vitium", "praecantatio"), List.of("seal.fluxclean.globe"), SealActions::clearFlux);
        register(List.of("ignis", "praecantatio", "ignis"), List.of("seal.smelt.globe"), SealActions::smelt);
        register(List.of("praecantatio", "spiritus", "sensus"), List.of("seal.whisper.globe"), SealActions::whisper);
        register(List.of("terra", "praecantatio"), List.of("seal.tillsoil.1.globe"), SealActions::soil);
        register(List.of("terra", "praecantatio", "aqua"), List.of("seal.tillsoil.1.globe", "seal.tillsoil.2.globe"), SealActions::soil);
        register(List.of("terra", "praecantatio", "perditio"), List.of("seal.breaksoil.1.globe"), SealActions::soil);
        register(List.of("vitium", "vitium", "vitium"), List.of("seal.warp.1.globe", "seal.warp.2.globe"), SealActions::warp);
        register(List.of("vacuos", "vacuos", "vacuos"), List.of("seal.collect.globe"), SealActions::collect);
        register(List.of("praecantatio", "meto", "herba"), List.of("seal.harveset.globe"), SealActions::harvest);
        register(List.of("machina", "tutamen", "praecantatio"), List.of("seal.mechprot.globe"), SealActions::protect);
        register(List.of("aer", "praecantatio", "praecantatio"), List.of("seal.teleportation.1.globe", "seal.teleportation.2.globe"), seal -> {});
        register(List.of("alienis", "praecantatio", "ignis"), new Effect(List.of("seal.teleport.-1.1.globe"), seal -> {}, SealActions::nether));
    }

    private SealBehaviors() {
    }

    private static void register(List<String> pattern, List<String> description, Consumer<SealBlockEntity> action) {
        register(pattern, new Effect(description, action, (seal, player) -> false));
    }

    public static void register(List<String> pattern, Effect effect) {
        if (pattern.isEmpty() || pattern.size() > 3 || EFFECTS.putIfAbsent(List.copyOf(pattern), effect) != null) {
            throw new IllegalArgumentException("Invalid or duplicate seal pattern: " + pattern);
        }
    }

    public static Effect find(SealData data) {
        return EFFECTS.getOrDefault(data.aspects(), NONE);
    }

    public record Effect(List<String> description, Consumer<SealBlockEntity> onTick,
        BiPredicate<SealBlockEntity, ServerPlayer> onActivate) {
        public Effect {
            description = List.copyOf(description);
        }

        public void tick(SealBlockEntity seal) {
            onTick.accept(seal);
        }

        public boolean activate(SealBlockEntity seal, ServerPlayer player) {
            return onActivate.test(seal, player);
        }
    }
}
