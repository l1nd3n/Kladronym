package dev.l1nd3n.kladronym.preprocess;

public final class Socrbase {
    private final AbbreviationCatalog abbreviations;
    private final LexemeAliases aliases;

    public Socrbase(AbbreviationCatalog abbreviations, LexemeAliases aliases) {
        this.abbreviations = abbreviations;
        this.aliases = aliases;
    }

    public AbbreviationCatalog abbreviations() {
        return abbreviations;
    }

    public LexemeAliases aliases() {
        return aliases;
    }
}
