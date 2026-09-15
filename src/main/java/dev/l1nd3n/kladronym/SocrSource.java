package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.source.FiasSource;
import dev.l1nd3n.kladronym.text.Abbreviations;
import dev.l1nd3n.kladronym.text.name.NormalizedString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public final class SocrSource implements FiasSource<Abbreviations> {
    private final FiasSource<Reader> input;

    public SocrSource(FiasSource<Reader> input) {
        this.input = input;
    }

    @Override
    public Abbreviations load() throws Exception {
        try (var reader = new BufferedReader(input.load())) {
            String header = reader.readLine();
            if (!"scname\tsocrname".equals(header)) throw new IOException("Unexpected SOCR header: " + header);
            Map<String, Set<String>> meanings = new LinkedHashMap<>();
            Map<String, String> spellings = new LinkedHashMap<>();
            Map<String, String> shortNames = new LinkedHashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t", -1);
                String shortName = columns[0];
                String fullName = columns[1];
                shortNames.merge(fullName, shortName, (current, candidate) ->
                        !current.endsWith(".") && candidate.endsWith(".") ? candidate : current);
                for (String spelling : List.of(shortName, fullName)) {
                    String normalized = new NormalizedString(spelling).get();
                    spellings.putIfAbsent(normalized, spelling);
                    meanings.computeIfAbsent(normalized, ignored -> new LinkedHashSet<>()).add(fullName);
                }
            }
            meanings.forEach((spelling, names) -> {
                if (names.size() == 1) spellings.put(spelling, shortNames.get(names.iterator().next()));
            });
            return new Abbreviations(meanings, spellings);
        }
    }

}
