package dev.l1nd3n.kladronym;

import java.util.List;

final class KladrCatalog {
    private final List<Kladronym> kladronyms;

    KladrCatalog(List<Kladronym> kladronyms) {
        this.kladronyms = kladronyms;
    }

    List<Kladronym> find(
        Toponym toponym,
        KladrCode scope,
        ToponymNameMatch nameMatch
    ) {
        return kladronyms.stream()
            .filter(candidate -> scope.contains(candidate.code()))
            .filter(candidate -> toponym.matches(candidate.toponym(), nameMatch))
            .toList();
    }
}
