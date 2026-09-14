package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.text.name.NormalizedString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class NormalizedStringTest {
    @Test
    void removesHyphenWithoutSplittingLexeme() {
        assertEquals(
                "автодорога екатеринбургтюмень",
                new NormalizedString("автодорога Екатеринбург-Тюмень").get()
        );
    }
}
