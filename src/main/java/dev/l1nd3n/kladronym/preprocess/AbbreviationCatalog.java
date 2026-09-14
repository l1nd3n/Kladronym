package dev.l1nd3n.kladronym.preprocess;

import java.util.Map;
import java.util.Optional;

public final class AbbreviationCatalog {
    private final Map<String, Abbreviation> bySpelling;

    public AbbreviationCatalog(Map<String, Abbreviation> bySpelling) {
        this.bySpelling = bySpelling;
    }

    public Optional<Abbreviation> find(String spelling) {
        String normalized = new NormalizedString(spelling).get();
        return Optional.ofNullable(bySpelling.get(normalized));
    }
}
