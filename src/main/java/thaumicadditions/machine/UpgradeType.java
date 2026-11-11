package thaumicadditions.machine;

public enum UpgradeType {
    SPEED,
    EFFICIENCY,
    ANGER,
    TAINT,
    SADNESS,
    CAPACITY,
    KNOWLEDGE;

    public String id() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
