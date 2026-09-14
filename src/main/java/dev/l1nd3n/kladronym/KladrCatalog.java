package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.name.NameMatch;
import dev.l1nd3n.kladronym.preprocess.Toponym;
import dev.l1nd3n.kladronym.kladr.KladrCode;

import java.util.List;

final class KladrCatalog {
    private final List<Kladronym> kladronyms;

    KladrCatalog(List<Kladronym> kladronyms) {
        this.kladronyms = kladronyms;
    }

    public List<Kladronym> find(
        Toponym toponym,
        KladrCode scope,
        NameMatch nameMatch
    ) {
        return kladronyms.stream()
            .filter(candidate -> scope.contains(candidate.code()))
            .filter(candidate -> toponym.matches(candidate.toponym(), nameMatch))
            .toList();
    }
}
