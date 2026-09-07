package dev.l1nd3n.kladronym;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class LoadedKladr {
    private static final String RESOURCE = "/dev/l1nd3n/kladronym/kladr.tsv";

    LoadedKladr() {
    }

    KladrCatalog get() {
        return Data.VALUE;
    }

    private static KladrCatalog read() {
        AbbreviationCatalog abbreviations = BundledData.SOCRBASE.get().abbreviations();
        try (BufferedReader reader = reader()) {
            List<Kladronym> kladronyms = reader.lines()
                .skip(1)
                .map(LoadedKladr::columns)
                .map(columns -> kladronym(columns, abbreviations))
                .sorted()
                .toList();
            return new KladrCatalog(kladronyms);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot load " + RESOURCE, exception);
        }
    }

    private static Kladronym kladronym(
        String[] columns,
        AbbreviationCatalog abbreviations
    ) {
        Abbreviation abbreviation = abbreviations.find(columns[1])
            .orElseThrow(() -> new IllegalStateException(
                "Unknown abbreviation: " + columns[1]
            ));
        return new Kladronym(
            new Toponym(columns[0], abbreviation),
            KladrCode.parse(columns[2])
        );
    }

    private static BufferedReader reader() {
        InputStream stream = LoadedKladr.class.getResourceAsStream(RESOURCE);
        if (stream == null) throw new IllegalStateException("Resource not found: " + RESOURCE);
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private static String[] columns(String line) {
        String[] columns = line.split("\\t", -1);
        if (columns.length != 3) {
            throw new IllegalStateException(
                "Expected 3 columns in %s, got %d: %s"
                    .formatted(RESOURCE, columns.length, line)
            );
        }
        return columns;
    }

    private static final class Data {
        private static final KladrCatalog VALUE = read();
    }
}
