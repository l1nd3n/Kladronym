package dev.l1nd3n.kladronym;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ResolvedAddress {
    private final String source;
    private final LoadedSocrbase socrbase;
    private final LoadedKladr kladr;
    private final ToponymNameMatch nameMatch;

    public ResolvedAddress(String source) {
        this(source, BundledData.NAME_MATCH, BundledData.SOCRBASE, BundledData.KLADR);
    }

    public ResolvedAddress(String source, ToponymNameMatch nameMatch) {
        this(source, nameMatch, BundledData.SOCRBASE, BundledData.KLADR);
    }

    private ResolvedAddress(String source, ToponymNameMatch nameMatch, LoadedSocrbase socrbase, LoadedKladr kladr) {
        this.source = source;
        this.nameMatch = nameMatch;
        this.socrbase = socrbase;
        this.kladr = kladr;
    }

    public Optional<Kladronym> get() {
        Socrbase loadedSocrbase = socrbase.get();
        KladrCatalog catalog = kladr.get();
        List<Token> tokens = new PreprocessedAddress(source, loadedSocrbase).get();
        int index = 0;
        KladrCode scope = KladrCode.ROOT;
        Kladronym result = null;
        Abbreviation expectedType = null;
        List<String> name = List.of();
        List<Kladronym> candidates = List.of();
        while (index < tokens.size()) {
            Token token = tokens.get(index);
            if (token.isAbbreviation()) {
                if (candidates.isEmpty()) {
                    expectedType = token.abbreviation();
                } else if (expectedType != null) {
                    result = candidates.getFirst();
                    scope = result.code();
                    expectedType = token.abbreviation();
                    name = List.of();
                    candidates = List.of();
                } else {
                    List<Kladronym> typed = catalog.find(
                        new Toponym(String.join(" ", name), token.abbreviation()),
                        scope,
                        nameMatch
                    );
                    if (typed.isEmpty()) {
                        result = candidates.getFirst();
                        expectedType = token.abbreviation();
                    } else {
                        result = typed.getFirst();
                        expectedType = null;
                    }
                    scope = result.code();
                    name = List.of();
                    candidates = List.of();
                }
                index++;
                continue;
            }
            boolean consumed = false;
            while (!consumed) {
                List<String> extendedName = extend(name, token.value());
                Toponym toponym = expectedType == null
                    ? new Toponym(String.join(" ", extendedName))
                    : new Toponym(String.join(" ", extendedName), expectedType);
                List<Kladronym> matched = catalog.find(toponym, scope, nameMatch);
                if (!matched.isEmpty()) {
                    name = extendedName;
                    candidates = matched;
                    consumed = true;
                } else if (!candidates.isEmpty()) {
                    result = candidates.getFirst();
                    scope = result.code();
                    expectedType = null;
                    name = List.of();
                    candidates = List.of();
                } else {
                    consumed = true;
                }
            }
            index++;
        }
        if (!candidates.isEmpty()) result = candidates.getFirst();
        return Optional.ofNullable(result);
    }

    private static List<String> extend(List<String> name, String word) {
        List<String> result = new ArrayList<>(name.size() + 1);
        result.addAll(name);
        result.add(word);
        return List.copyOf(result);
    }

}
