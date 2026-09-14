package dev.l1nd3n.kladronym.preprocess;

import java.util.List;

public final class PreprocessedAddress {
    private final String source;
    private final Socrbase socrbase;

    public PreprocessedAddress(String source, Socrbase socrbase) {
        this.source = source;
        this.socrbase = socrbase;
    }

    public List<Token> get() {
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
