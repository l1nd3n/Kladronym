package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.code.KladrRank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

final class KladrCodeTest {
    @Test
    void returnsPrefixForRank() {
        KladrCode code = new KladrCode(1, 2, 3, 4);
        assertEquals("01", code.prefix(KladrRank.REGION));
        assertEquals("01002", code.prefix(KladrRank.DISTRICT));
        assertEquals("01002003", code.prefix(KladrRank.CITY));
        assertEquals("01002003004", code.prefix(KladrRank.LOCALITY));
    }

    @Test
    void comparesCodesByValue() {
        KladrCode code = new KladrCode(64, 0, 1, 0);
        KladrCode equal = new KladrCode(64, 0, 1, 0);
        KladrCode different = new KladrCode(64, 0, 2, 0);
        assertEquals(code, equal);
        assertEquals(code.hashCode(), equal.hashCode());
        assertNotEquals(code, different);
    }
}
