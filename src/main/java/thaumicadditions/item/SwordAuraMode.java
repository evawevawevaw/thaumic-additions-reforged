package thaumicadditions.item;

public enum SwordAuraMode {
    QUICK(5, 10),
    BALANCED(15, 20),
    HEAVY(30, 60);

    private final int damage;
    private final int interval;

    SwordAuraMode(int damage, int interval) {
        this.damage = damage;
        this.interval = interval;
    }

    public int damage() {
        return damage;
    }

    public int interval() {
        return interval;
    }

    public SwordAuraMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static SwordAuraMode fromIndex(int index) {
        return index >= 0 && index < values().length ? values()[index] : QUICK;
    }
}
