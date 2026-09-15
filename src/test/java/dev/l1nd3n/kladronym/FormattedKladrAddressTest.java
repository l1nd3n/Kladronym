package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.Kladronym;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FormattedKladrAddressTest {
    @Test
    void formatsRealHierarchyWithoutInventingZeroLevels() throws Exception {
        var kladr = new KladrSource().load();
        var types = new SocrSource().load();
        assertEquals("Саратовская (Область), Саратов (Город)",
                new FormattedKladrAddress(new KladrCode("64000001000"), kladr, types).get());
        assertEquals("Москва (Город)",
                new FormattedKladrAddress(new KladrCode("77000000000"), kladr, types).get());
    }

    @Test
    void visitsEverySignificantLevelInOrder() throws Exception {
        var region = new KladrCode("64000000000");
        var district = new KladrCode("64001000000");
        var city = new KladrCode("64001001000");
        var locality = new KladrCode("64001001001");
        var kladr = new Kladr(List.of(
                new Kladronym(new Toponym("Лесной", "п"), locality),
                new Kladronym(new Toponym("Городской", "г"), city),
                new Kladronym(new Toponym("Районный", "р-н"), district),
                new Kladronym(new Toponym("Областная", "обл"), region)));
        assertEquals("Областная (Область), Районный (Район), Городской (Город), Лесной (Поселение/Поселок)",
                new FormattedKladrAddress(locality, kladr, new SocrSource().load()).get());
        assertEquals(locality, kladr.find(locality).orElseThrow().code());
        assertTrue(kladr.find(new KladrCode("65000000000")).isEmpty());
        var match = new StandardToponymMatch((query, candidate) -> true, new SocrSource().load());
        assertEquals(List.of(region, district, city, locality),
                kladr.find(new Toponym(""), KladrCode.ROOT, match).stream().map(Kladronym::code).toList());
    }

    @Test
    void formatsUnknownAndAbsentTypesWithoutChangingToponym() throws Exception {
        var types = new SocrSource().load();
        var unknown = new Kladronym(new Toponym("Имя", "неизвестный"), new KladrCode("64000000000"));
        assertEquals("Имя (неизвестный)", new FormattedKladronym(unknown, types).get());
        var absent = new Kladronym(new Toponym("Имя"), unknown.code());
        assertEquals("Имя", new FormattedKladronym(absent, types).get());
    }
}
