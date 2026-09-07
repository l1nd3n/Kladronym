package dev.l1nd3n.kladronym;

import java.util.List;

final class CanonicalizedAddress {
    private final LexedAddress address;
    private final LexemeAliases aliases;

    CanonicalizedAddress(LexedAddress address, LexemeAliases aliases) {
        this.address = address;
        this.aliases = aliases;
    }

    List<String> get() {
        return aliases.replace(address.get());
    }
}
