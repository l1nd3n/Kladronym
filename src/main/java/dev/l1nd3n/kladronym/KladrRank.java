package dev.l1nd3n.kladronym;

public enum KladrRank {
    REGION(0),
    DISTRICT(1),
    CITY(2),
    LOCALITY(3);

    private final int value;

    KladrRank(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
