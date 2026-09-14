package dev.l1nd3n.kladronym.preprocess;

import dev.l1nd3n.kladronym.name.NameMatch;

public final class Toponym {
    private final String name;
    private final Abbreviation abbreviation;

    public Toponym(String name) {
        this.name = name;
        this.abbreviation = null;
    }

    public Toponym(String name, Abbreviation abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public boolean matches(Toponym candidate, NameMatch nameMatch) {
        if (!nameMatch.matches(name, candidate.name)) return false;
        if (abbreviation == null || candidate.abbreviation == null) return true;
        return abbreviation.fullNames().stream()
            .anyMatch(candidate.abbreviation.fullNames()::contains);
    }

    @Override
    public String toString() {
        return abbreviation == null ? name : "%s (%s)".formatted(name, abbreviation);
    }
}
