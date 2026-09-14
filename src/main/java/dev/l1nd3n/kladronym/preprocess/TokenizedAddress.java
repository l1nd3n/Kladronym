package dev.l1nd3n.kladronym.preprocess;

import java.util.List;

public final class TokenizedAddress {
    private final CanonicalizedAddress address;
    private final AbbreviationCatalog abbreviations;

    public TokenizedAddress(CanonicalizedAddress address, AbbreviationCatalog abbreviations) {
        this.address = address;
        this.abbreviations = abbreviations;
    }

    public List<Token> get() {
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
