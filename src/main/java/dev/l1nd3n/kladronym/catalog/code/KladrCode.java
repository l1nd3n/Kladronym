package dev.l1nd3n.kladronym.catalog.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class KladrCode {
    public static final KladrCode ROOT = new KladrCode(0, 0, 0, 0);

    private final int rc;
    private final int dc;
    private final int cc;
    private final int lc;

    public KladrCode(int rc, int dc, int cc, int lc) {
        this.rc = rc;
        this.dc = dc;
        this.cc = cc;
        this.lc = lc;
    }

    public KladrCode(String kladrCode) {
        this(
                Integer.parseInt(kladrCode, 0, 2, 10),
                Integer.parseInt(kladrCode, 2, 5, 10),
                Integer.parseInt(kladrCode, 5, 8, 10),
                Integer.parseInt(kladrCode, 8, 11, 10)
        );
    }

    /**
     * Significant levels from the region to this code, including this code.
     */
    public List<KladrCode> hierarchy() {
        List<KladrCode> levels = new ArrayList<>();
        if (rc > 0) levels.add(new KladrCode(rc, 0, 0, 0));
        if (dc > 0) levels.add(new KladrCode(rc, dc, 0, 0));
        if (cc > 0) levels.add(new KladrCode(rc, dc, cc, 0));
        if (lc > 0) levels.add(new KladrCode(rc, dc, cc, lc));
        return List.copyOf(levels);
    }

    public KladrRank rank() {
        if (lc > 0) return KladrRank.LOCALITY;
        if (cc > 0) return KladrRank.CITY;
        if (dc > 0) return KladrRank.DISTRICT;
        if (rc > 0) return KladrRank.REGION;
        throw new IllegalStateException("KLADR code has no rank: " + this);
    }

    public String prefix(KladrRank rank) {
        if (rc == 0) {
            throw new IllegalStateException("Root KLADR code has no prefix");
        }
        return switch (rank) {
            case REGION -> "%02d".formatted(rc);
            case DISTRICT -> "%02d%03d".formatted(rc, dc);
            case CITY -> "%02d%03d%03d".formatted(rc, dc, cc);
            case LOCALITY -> "%02d%03d%03d%03d".formatted(rc, dc, cc, lc);
        };
    }

    public boolean contains(KladrCode other) {
        if (other.equals(ROOT)) return false;
        if (equals(ROOT)) return true;
        KladrRank thisRank = rank();
        KladrRank otherRank = other.rank();
        if (otherRank.compareTo(thisRank) <= 0) return false;
        return switch (thisRank) {
            case REGION -> rc == other.rc;
            case DISTRICT -> rc == other.rc && dc == other.dc;
            case CITY -> rc == other.rc && dc == other.dc && cc == other.cc;
            case LOCALITY -> false;
        };
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof KladrCode code)) return false;
        return rc == code.rc && dc == code.dc && cc == code.cc && lc == code.lc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rc, dc, cc, lc);
    }

    @Override
    public String toString() {
        return prefix(KladrRank.LOCALITY);
    }
}
