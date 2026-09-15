package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.code.KladrRank;
import dev.l1nd3n.kladronym.text.Abbreviations;

import java.util.Arrays;
import java.util.Optional;
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
        return Arrays.stream(KladrRank.values())
                .map(code::atRank)
                .distinct()
                .map(kladr::find)
                .flatMap(Optional::stream)
                .map(found -> new FormattedKladronym(found, abbreviations).get())
                .collect(Collectors.joining(", "));
    }
}
