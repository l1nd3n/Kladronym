package dev.l1nd3n.kladronym.preprocess;

public final class Token {
    private final TokenType type;
    private final String value;
    private final Abbreviation abbreviation;

    public Token(TokenType type, String value, Abbreviation abbreviation) {
        this.type = type;
        this.value = value;
        this.abbreviation = abbreviation;
    }

    public boolean isAbbreviation() {
        return type == TokenType.ABBREVIATION;
    }

    public String value() {
        return value;
    }

    public Abbreviation abbreviation() {
        return abbreviation;
    }
}
