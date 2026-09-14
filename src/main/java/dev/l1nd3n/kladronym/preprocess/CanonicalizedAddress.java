package dev.l1nd3n.kladronym.preprocess;

import java.util.List;

public final class CanonicalizedAddress {
    private final LexedAddress address;
    private final LexemeAliases aliases;

    public CanonicalizedAddress(LexedAddress address, LexemeAliases aliases) {
        this.address = address;
        this.aliases = aliases;
    }

    public List<String> get() {
        return aliases.replace(address.get());
    }
}
