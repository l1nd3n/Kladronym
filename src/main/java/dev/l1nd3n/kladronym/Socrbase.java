package dev.l1nd3n.kladronym;

final class Socrbase {
    private final AbbreviationCatalog abbreviations;
    private final LexemeAliases aliases;

    Socrbase(AbbreviationCatalog abbreviations, LexemeAliases aliases) {
        this.abbreviations = abbreviations;
        this.aliases = aliases;
    }

    AbbreviationCatalog abbreviations() {
        return abbreviations;
    }

    LexemeAliases aliases() {
        return aliases;
    }
}
