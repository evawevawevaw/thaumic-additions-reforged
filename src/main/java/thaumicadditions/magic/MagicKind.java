package thaumicadditions.magic;

public enum MagicKind {
    VIS(7078127),
    TAINT(4194497);

    private final int color;

    MagicKind(int color) {
        this.color = color;
    }

    public int color() {
        return color;
    }
}
