package dev.l1nd3n.kladronym;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class NormalizedStringTest {
    @Test
    void removesHyphenWithoutSplittingLexeme() {
        assertEquals(
            "автодорога екатеринбургтюмень",
            new NormalizedString("автодорога Екатеринбург-Тюмень").get()
        );
    }
}
