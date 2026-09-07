package dev.l1nd3n.kladronym;

final class Token {
    private final TokenType type;
    private final String value;
    private final Abbreviation abbreviation;

    Token(TokenType type, String value, Abbreviation abbreviation) {
        this.type = type;
        this.value = value;
        this.abbreviation = abbreviation;
    }

    boolean isAbbreviation() {
        return type == TokenType.ABBREVIATION;
    }

    String value() {
        return value;
    }

    Abbreviation abbreviation() {
        return abbreviation;
    }
}
