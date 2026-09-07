package dev.l1nd3n.kladronym;

import java.util.List;

final class PreprocessedAddress {
    private final String source;
    private final Socrbase socrbase;

    PreprocessedAddress(String source, Socrbase socrbase) {
        this.source = source;
        this.socrbase = socrbase;
    }

    List<Token> get() {
        return preprocess();
    }

    private List<Token> preprocess() {
        NormalizedString normalized = new NormalizedString(source);
        LexedAddress lexed = new LexedAddress(normalized);
        CanonicalizedAddress canonicalized = new CanonicalizedAddress(
            lexed,
            socrbase.aliases()
        );
        TokenizedAddress tokenized = new TokenizedAddress(
            canonicalized,
            socrbase.abbreviations()
        );
        return tokenized.get();
    }
}
