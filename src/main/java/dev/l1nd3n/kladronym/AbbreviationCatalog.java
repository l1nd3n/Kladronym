package dev.l1nd3n.kladronym;

import java.util.Map;
import java.util.Optional;

final class AbbreviationCatalog {
    private final Map<String, Abbreviation> bySpelling;

    AbbreviationCatalog(Map<String, Abbreviation> bySpelling) {
        this.bySpelling = bySpelling;
    }

    Optional<Abbreviation> find(String spelling) {
        String normalized = new NormalizedString(spelling).get();
        return Optional.ofNullable(bySpelling.get(normalized));
    }
}
