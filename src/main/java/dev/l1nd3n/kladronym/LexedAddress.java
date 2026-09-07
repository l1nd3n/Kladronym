package dev.l1nd3n.kladronym;

import java.util.List;

final class LexedAddress {
    private final NormalizedString address;

    LexedAddress(NormalizedString address) {
        this.address = address;
    }

    List<String> get() {
        return lex();
    }

    private List<String> lex() {
        String normalized = address.get();
        if (normalized.isEmpty()) return List.of();
        return List.of(normalized.split(" "));
    }
}
