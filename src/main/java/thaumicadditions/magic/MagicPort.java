package thaumicadditions.magic;

import net.minecraft.core.Direction;

public interface MagicPort {
    boolean canConnect(Direction side);

    int stored(MagicKind kind);

    int insert(MagicKind kind, int amount, Direction side, boolean simulate);

    int extract(MagicKind kind, int amount, Direction side, boolean simulate);
}
