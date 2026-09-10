package dev.l1nd3n.kladronym;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

final class LikelyKladronymDirectionTest {
    @Test
    void resetsUnmatchedAbbreviation() {
        Map<String, String> addresses = Map.of(
            "д Богданово, Рамонский район, Воронежская область, Россия", "36026000000",
            "автодорога Екатеринбург-Тюмень, Белоярский район, Свердловская область, Россия", "66007000000",
            "автодорога Волковское-Ильинское, Богдановичский район, Свердловская область, Россия", "66008000000",
            "п Красный Восток, Новосибирский район, Новосибирская область, Россия", "54001000000",
            "рп Татищево, Татищевский район, Саратовская область, Россия", "64035000000",
            "п Увельский, Увельский район, Челябинская область, Россия", "74040000000"
        );
        addresses.forEach((address, expected) -> assertEquals(
            expected,
            new LikelyKladronym(address).get().orElseThrow().code().toString(),
            address
        ));
    }

    @Test
    void retriesInReverseWhenForwardPassIsEmpty() {
        String address = "Богданово д, Рамонский район, Воронежская область, Россия";
        assertEquals(
            "36026000000",
            new LikelyKladronym(address).get().orElseThrow().code().toString()
        );
        assertEquals(
            "52001000000",
            new LikelyKladronym("Новгород г, Нижегородская область, Россия")
                .get()
                .orElseThrow()
                .code()
                .toString()
        );
    }
}
