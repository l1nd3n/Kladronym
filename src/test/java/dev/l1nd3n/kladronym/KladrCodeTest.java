package dev.l1nd3n.kladronym;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class KladrCodeTest {
    @Test
    void returnsPrefixForRank() {
        KladrCode code = new KladrCode(1,2,3,4);
        assertEquals("01",code.prefix(KladrRank.REGION));
        assertEquals("01002",code.prefix(KladrRank.DISTRICT));
        assertEquals("01002003",code.prefix(KladrRank.CITY));
        assertEquals("01002003004",code.prefix(KladrRank.LOCALITY));
    }
}
