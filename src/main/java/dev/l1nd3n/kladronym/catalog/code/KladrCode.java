package dev.l1nd3n.kladronym.catalog.code;

import java.util.Objects;

/**
 * An 11-digit hierarchical KLADR code in the format {@code RR + DDD + CCC + LLL}.
 * <ul>
 *     <li>{@code RR} — Russian federal subject (region) code, 2 digits;</li>
 *     <li>{@code DDD} — district code, 3 digits;</li>
 *     <li>{@code CCC} — city (rural settlement) code, 3 digits;</li>
 *     <li>{@code LLL} — locality code, 3 digits.</li>
 * </ul>
 * <p>Zero-valued components do not represent separate hierarchy levels. For example,
 * {@code 64|000|001|000} identifies an object at the city level in region
 * {@code 64}, with no district level.
 */
public final class KladrCode {
    /**
     * Virtual root for searching the entire catalog; does not identify a real object.
     */
    public static final KladrCode ROOT = new KladrCode(0, 0, 0, 0);

    private final int rc;
    private final int dc;
    private final int cc;
    private final int lc;

    /**
     * Creates a code from its four components.
     *
     * @param rc region code ({@code RR})
     * @param dc district code ({@code DDD})
     * @param cc city or rural settlement code ({@code CCC})
     * @param lc locality code ({@code LLL})
     */
    public KladrCode(int rc, int dc, int cc, int lc) {
        this.rc = rc;
        this.dc = dc;
        this.cc = cc;
        this.lc = lc;
    }

    /**
     * Parses the string representation of a code.
     *
     * @param kladrCode code in the format {@code RRDDDCCCLLL}, without separators
     */
    public KladrCode(String kladrCode) {
        this(
                Integer.parseInt(kladrCode, 0, 2, 10),
                Integer.parseInt(kladrCode, 2, 5, 10),
                Integer.parseInt(kladrCode, 5, 8, 10),
                Integer.parseInt(kladrCode, 8, 11, 10)
        );
    }

    /**
     * Returns a code with all components below the specified level set to zero.
     * If that level has a zero component, the result may identify a higher level.
     * Requesting a level below this code's rank leaves its value unchanged.
     *
     * @param rank the last level to retain
     * @return the code truncated to the requested level; {@link #ROOT} remains root
     */
    public KladrCode atRank(KladrRank rank) {
        return switch (rank) {
            case REGION -> new KladrCode(rc, 0, 0, 0);
            case DISTRICT -> new KladrCode(rc, dc, 0, 0);
            case CITY -> new KladrCode(rc, dc, cc, 0);
            case LOCALITY -> new KladrCode(rc, dc, cc, lc);
        };
    }

    /**
     * Determines the level from the last nonzero component of the code.
     *
     * @return the region, district, city, or locality level
     * @throws IllegalStateException if the code has no significant level
     */
    public KladrRank rank() {
        if (lc > 0) return KladrRank.LOCALITY;
        if (cc > 0) return KladrRank.CITY;
        if (dc > 0) return KladrRank.DISTRICT;
        if (rc > 0) return KladrRank.REGION;
        throw new IllegalStateException("KLADR code has no rank: " + this);
    }

    /**
     * Returns the code prefix through the specified level, inclusive,
     * preserving leading zeros in each component.
     *
     * @param rank the last level to include
     * @return a string of 2, 5, 8, or 11 digits
     * @throws IllegalStateException if the region code is zero
     */
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

    /**
     * Checks whether another code is strictly below this code in the same branch.
     * A code does not contain itself; {@link #ROOT} contains every non-root code.
     *
     * @param other the code to check
     * @return {@code true} if the other code belongs to a lower level of this branch
     */
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
