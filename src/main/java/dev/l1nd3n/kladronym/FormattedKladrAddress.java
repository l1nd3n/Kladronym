package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.text.Abbreviations;

import java.util.stream.Collectors;

public final class FormattedKladrAddress {
    private final KladrCode code;
    private final Kladr kladr;
    private final Abbreviations abbreviations;

    public FormattedKladrAddress(KladrCode code, Kladr kladr, Abbreviations abbreviations) {
        this.code = code;
        this.kladr = kladr;
        this.abbreviations = abbreviations;
    }

    public String get() {
        return code.hierarchy().stream()
                .map(level -> kladr.find(level).orElseThrow())
                .map(found -> new FormattedKladronym(found, abbreviations).get())
                .collect(Collectors.joining(", "));
    }
}
