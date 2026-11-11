package thaumicadditions.machine;

public final class VisEnergyBuffer {
    public static final int CAPACITY = 640000;
    public static final int TRANSFER_LIMIT = 2000;
    private static final int DENOMINATOR = 6;
    private static final int PER_VIS = 5000;
    private int stored;
    private boolean exportingMana;

    public boolean canConvertVis() {
        return !exportingMana && stored <= CAPACITY * DENOMINATOR - PER_VIS;
    }

    public boolean convertVis() {
        if (!canConvertVis()) {
            return false;
        }
        stored += PER_VIS;
        return true;
    }

    public int energyStored() {
        return stored / DENOMINATOR;
    }

    public int extract(int requested, boolean simulate) {
        int extracted = Math.min(Math.max(0, requested), Math.min(TRANSFER_LIMIT, energyStored()));
        if (!simulate) {
            stored -= extracted * DENOMINATOR;
        }
        return extracted;
    }

    public int savedUnits() {
        return stored;
    }

    public int emitMana(java.util.function.IntUnaryOperator receiver) {
        int offer = Math.min(TRANSFER_LIMIT, stored / (10 * DENOMINATOR));
        if (offer == 0 || exportingMana) {
            return 0;
        }
        stored -= offer * 10 * DENOMINATOR;
        exportingMana = true;
        try {
            int accepted = Math.clamp(receiver.applyAsInt(offer), 0, offer);
            stored += (offer - accepted) * 10 * DENOMINATOR;
            return accepted;
        } finally {
            exportingMana = false;
        }
    }

    public void loadUnits(int units) {
        stored = Math.clamp(units, 0, CAPACITY * DENOMINATOR);
    }
}
