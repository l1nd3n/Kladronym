package dev.l1nd3n.kladronym.catalog;

import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.catalog.toponym.ToponymMatch;

import java.util.List;
import java.util.Optional;

public final class Kladr {
    private final List<Kladronym> kladronyms;

    public Kladr(List<Kladronym> kladronyms) {
        this.kladronyms = List.copyOf(kladronyms);
    }

    public Optional<Kladronym> find(KladrCode code) {
        return kladronyms.stream().filter(candidate -> code.equals(candidate.code())).findFirst();
    }

    public List<Kladronym> find(
            Toponym toponym,
            KladrCode scope,
            ToponymMatch match
    ) {
        return kladronyms.stream()
                .filter(candidate -> scope.contains(candidate.code()))
                .filter(candidate -> toponym.matches(candidate.toponym(), match))
                .sorted()
                .toList();
    }
}
