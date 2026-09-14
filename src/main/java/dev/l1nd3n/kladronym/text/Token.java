package dev.l1nd3n.kladronym.text;

public final class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public boolean isAbbreviation() {
        return type == TokenType.ABBREVIATION;
    }

    public String value() {
        return value;
    }
}
