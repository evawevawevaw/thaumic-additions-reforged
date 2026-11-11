package thaumicadditions.menu;

public final class MachineMenuLayout {
    public static final int HEADER = 18;
    public static final int CRYSTALLIZER_BODY = 222;
    public static final int DARK_INVENTORY_GAP = 14;

    private MachineMenuLayout() {
    }

    public static int crystallizerY(int y) {
        return HEADER + Math.round(y * CRYSTALLIZER_BODY / 240.0F);
    }
}
