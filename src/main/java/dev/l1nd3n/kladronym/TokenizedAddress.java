package dev.l1nd3n.kladronym;

import java.util.List;

final class TokenizedAddress {
    private final CanonicalizedAddress address;
    private final AbbreviationCatalog abbreviations;

    TokenizedAddress(CanonicalizedAddress address, AbbreviationCatalog abbreviations) {
        this.address = address;
        this.abbreviations = abbreviations;
    }

    List<Token> get() {
        return tokenize();
    }

    private List<Token> tokenize() {
        return address.get().stream()
            .map(lexeme -> abbreviations.find(lexeme)
                .map(abbreviation -> new Token(
                    TokenType.ABBREVIATION,
                    lexeme,
                    abbreviation
                ))
                .orElseGet(() -> new Token(TokenType.TEXT, lexeme, null)))
            .toList();
    }
}
