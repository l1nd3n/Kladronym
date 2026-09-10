package dev.l1nd3n.kladronym;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

final class ResolutionPass {
    private final List<Token> tokens;
    private final KladrCatalog catalog;
    private final ToponymNameMatch nameMatch;

    ResolutionPass(List<Token> tokens, KladrCatalog catalog, ToponymNameMatch nameMatch) {
        this.tokens = tokens;
        this.catalog = catalog;
        this.nameMatch = nameMatch;
    }

    Optional<Kladronym> frontwards() {
        return resolve(tokens, nameMatch, ExtendedName::frontwards);
    }

    Optional<Kladronym> backwards() {
        return resolve(tokens.reversed(), new ReversedNameMatch(nameMatch), ExtendedName::backwards);
    }

    private Optional<Kladronym> resolve(
        List<Token> tokens,
        ToponymNameMatch nameMatch,
        Function<ExtendedName, String> extendName
    ) {
        int index = 0;
        KladrCode scope = KladrCode.ROOT;
        Kladronym result = null;
        Abbreviation expectedType = null;
        String name = "";
        List<Kladronym> candidates = List.of();
        while (index < tokens.size()) {
            Token token = tokens.get(index);
            if (token.isAbbreviation()) {
                if (candidates.isEmpty()) {
                    expectedType = token.abbreviation();
                } else if (expectedType != null) {
                    result = candidates.getFirst();
                    scope = result.code();
                    expectedType = token.abbreviation();
                    name = "";
                    candidates = List.of();
                } else {
                    List<Kladronym> typed = catalog.find(
                        new Toponym(name, token.abbreviation()),
                        scope,
                        nameMatch
                    );
                    if (typed.isEmpty()) {
                        result = candidates.getFirst();
                        expectedType = token.abbreviation();
                    } else {
                        result = typed.getFirst();
                        expectedType = null;
                    }
                    scope = result.code();
                    name = "";
                    candidates = List.of();
                }
                index++;
                continue;
            }
            boolean consumed = false;
            while (!consumed) {
                ExtendedName extension = new ExtendedName(name, token.value());
                String extendedName = extendName.apply(extension);
                Toponym toponym = expectedType == null
                    ? new Toponym(extendedName)
                    : new Toponym(extendedName, expectedType);
                List<Kladronym> matched = catalog.find(toponym, scope, nameMatch);
                if (!matched.isEmpty()) {
                    name = extendedName;
                    candidates = matched;
                    consumed = true;
                } else if (!candidates.isEmpty()) {
                    result = candidates.getFirst();
                    scope = result.code();
                    expectedType = null;
                    name = "";
                    candidates = List.of();
                } else {
                    expectedType = null;
                    consumed = true;
                }
            }
            index++;
        }
        if (!candidates.isEmpty()) result = candidates.getFirst();
        return Optional.ofNullable(result);
    }
}
