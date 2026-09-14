package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.preprocess.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class LoadedSocrbase {
    private static final String RESOURCE = "/dev/l1nd3n/kladronym/socrbase.tsv";

    LoadedSocrbase() {
    }

    Socrbase get() {
        return Data.VALUE;
    }

    private static Socrbase read() {
        try (BufferedReader reader = reader()) {
            Map<String, List<String>> fullNamesBySpelling = new LinkedHashMap<>();
            Map<String, String> originalSpelling = new HashMap<>();
            Map<String, String> canonicalShortName = new LinkedHashMap<>();
            reader.lines()
                .skip(1)
                .map(LoadedSocrbase::columns)
                .forEach(columns -> collect(
                    columns,
                    fullNamesBySpelling,
                    originalSpelling,
                    canonicalShortName
                ));
            Map<String, Abbreviation> abbreviations = abbreviations(
                fullNamesBySpelling,
                originalSpelling,
                canonicalShortName
            );
            AbbreviationCatalog catalog = new AbbreviationCatalog(abbreviations);
            LexemeAliases aliases = LexemeAliases.create(abbreviations);
            return new Socrbase(catalog, aliases);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot load " + RESOURCE, exception);
        }
    }

    private static void collect(
        String[] columns,
        Map<String, List<String>> fullNamesBySpelling,
        Map<String, String> originalSpelling,
        Map<String, String> canonicalShortName
    ) {
        String shortName = columns[0];
        String fullName = columns[1];
        canonicalShortName.merge(fullName, shortName, LoadedSocrbase::preferredShortName);
        addSpelling(fullNamesBySpelling, originalSpelling, shortName, fullName);
        addSpelling(fullNamesBySpelling, originalSpelling, fullName, fullName);
    }

    private static Map<String, Abbreviation> abbreviations(
        Map<String, List<String>> fullNamesBySpelling,
        Map<String, String> originalSpelling,
        Map<String, String> canonicalShortName
    ) {
        Map<String, Abbreviation> canonicalByFullName = new HashMap<>();
        canonicalShortName.forEach((fullName, shortName) -> canonicalByFullName.put(
            fullName,
            new Abbreviation(List.of(fullName), shortName)
        ));
        Map<String, Abbreviation> result = new HashMap<>();
        fullNamesBySpelling.forEach((spelling, fullNames) -> {
            Abbreviation abbreviation = fullNames.size() == 1
                ? canonicalByFullName.get(fullNames.getFirst())
                : new Abbreviation(List.copyOf(fullNames), originalSpelling.get(spelling));
            result.put(spelling, abbreviation);
        });
        return Map.copyOf(result);
    }

    private static void addSpelling(
        Map<String, List<String>> fullNamesBySpelling,
        Map<String, String> originalSpelling,
        String spelling,
        String fullName
    ) {
        String normalized = new NormalizedString(spelling).get();
        originalSpelling.putIfAbsent(normalized, spelling);
        List<String> fullNames = fullNamesBySpelling.computeIfAbsent(
            normalized,
            ignored -> new ArrayList<>()
        );
        if (!fullNames.contains(fullName)) fullNames.add(fullName);
    }

    private static BufferedReader reader() {
        InputStream stream = LoadedSocrbase.class.getResourceAsStream(RESOURCE);
        if (stream == null) throw new IllegalStateException("Resource not found: " + RESOURCE);
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private static String[] columns(String line) {
        String[] columns = line.split("\\t", -1);
        if (columns.length != 2) {
            throw new IllegalStateException(
                "Expected 2 columns in %s, got %d: %s"
                    .formatted(RESOURCE, columns.length, line)
            );
        }
        return columns;
    }

    private static String preferredShortName(String current, String candidate) {
        if (!current.endsWith(".") && candidate.endsWith(".")) return candidate;
        return current;
    }

    private static final class Data {
        private static final Socrbase VALUE = read();
    }
}
