package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladronym;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.text.Abbreviations;

public final class FormattedKladronym {
    private final Kladronym kladronym;
    private final Abbreviations abbreviations;

    public FormattedKladronym(Kladronym kladronym, Abbreviations abbreviations) {
        this.kladronym = kladronym;
        this.abbreviations = abbreviations;
    }

    public String get() {
        Toponym toponym = kladronym.toponym();
        String type = abbreviations.find(toponym.type())
                .map(names -> String.join("/", names)).orElse(toponym.type());
        return new Toponym(toponym.name(), type).toString();
    }
}
