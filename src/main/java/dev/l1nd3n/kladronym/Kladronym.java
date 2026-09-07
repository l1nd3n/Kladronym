package dev.l1nd3n.kladronym;

public final class Kladronym implements Comparable<Kladronym> {
    private final Toponym toponym;
    private final KladrCode code;

    public Kladronym(Toponym toponym, KladrCode code) {
        this.toponym = toponym;
        this.code = code;
    }

    public Toponym toponym() {
        return toponym;
    }

    public KladrCode code() {
        return code;
    }

    @Override
    public int compareTo(Kladronym other) {
        int byRank = Integer.compare(code.rank().value(), other.code.rank().value());
        if (byRank != 0) return byRank;
        return code.toString().compareTo(other.code.toString());
    }
}
