package thaumicadditions.magic;

import net.minecraft.core.Direction;

public final class MagicTransfer {
    private MagicTransfer() {
    }

    public static int move(MagicPort source, MagicPort destination, MagicKind kind, int limit, Direction direction) {
        if (limit <= 0 || source == destination || !source.canConnect(direction)
            || !destination.canConnect(direction.getOpposite())) {
            return 0;
        }
        int available = source.extract(kind, limit, direction, true);
        int amount = destination.insert(kind, available, direction.getOpposite(), true);
        if (amount <= 0) {
            return 0;
        }
        int extracted = source.extract(kind, amount, direction, false);
        int inserted = destination.insert(kind, extracted, direction.getOpposite(), false);
        if (inserted < extracted) {
            source.insert(kind, extracted - inserted, direction, false);
        }
        return inserted;
    }
}
