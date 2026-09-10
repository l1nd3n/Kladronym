package dev.l1nd3n.kladronym;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

final class LikelyKladronymRegressionTest {
    private static final String RESOURCE_ROOT = "/dev/l1nd3n/kladronym/";

    @Test
    void resolvesMockAddressesExactlyAsExpected() throws Exception {
        List<String> expected = resourceLines("result.tsv");
        List<String> actual = createResult(resourceLines("mock_addresses"));
        int commonLines = Math.min(expected.size(), actual.size());
        for (int index = 0; index < commonLines; index++) {
            assertEquals(expected.get(index), actual.get(index), "Difference at line " + (index + 1));
        }
        assertEquals(expected.size(), actual.size(), "Different number of lines");
    }

    private static List<String> createResult(List<String> addresses) {
        List<String> result = new ArrayList<>();
        result.add("address\ttoponym\tcode");
        for (String address : addresses) {
            Kladronym kladronym = new LikelyKladronym(address).get().orElse(null);
            String toponym = kladronym == null ? "" : kladronym.toponym().toString();
            String code = kladronym == null ? "" : kladronym.code().toString();
            result.add("%s\t%s\t%s".formatted(address, toponym, code));
        }
        return result;
    }

    private static List<String> resourceLines(String name) throws Exception {
        String resource = RESOURCE_ROOT + name;
        InputStream stream = LikelyKladronymRegressionTest.class.getResourceAsStream(resource);
        if (stream == null) throw new AssertionError("Resource not found: " + resource);
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(stream, StandardCharsets.UTF_8)
        )) {
            return reader.lines().toList();
        }
    }
}
