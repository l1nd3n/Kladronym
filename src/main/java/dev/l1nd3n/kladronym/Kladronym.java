package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.preprocess.Toponym;
import dev.l1nd3n.kladronym.kladr.KladrCode;

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
        int byRank = code.rank().compareTo(other.code.rank());
        if (byRank != 0) return byRank;
        return code.toString().compareTo(other.code.toString());
    }
}
