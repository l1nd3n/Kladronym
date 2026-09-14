package dev.l1nd3n.kladronym.preprocess;

import java.util.List;

public final class Abbreviation {
    private final List<String> fullNames;
    private final String shortName;

    public Abbreviation(List<String> fullNames, String shortName) {
        this.fullNames = fullNames;
        this.shortName = shortName;
    }

    public List<String> fullNames() {
        return fullNames;
    }

    public String shortName() {
        return shortName;
    }

    @Override
    public String toString() {
        return String.join("/", fullNames);
    }
}
