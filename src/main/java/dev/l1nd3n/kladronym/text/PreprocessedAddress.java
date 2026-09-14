package dev.l1nd3n.kladronym.text;

import dev.l1nd3n.kladronym.text.name.NormalizedString;

import java.util.List;

public final class PreprocessedAddress {
    private final String source;
    private final Aliases aliases;

    public PreprocessedAddress(String source, Aliases aliases) {
        this.source = source;
        this.aliases = aliases;
    }

    public List<Token> get() {
        return new TokenizedAddress(new LexedAddress(new NormalizedString(source)), aliases).get();
    }
}
