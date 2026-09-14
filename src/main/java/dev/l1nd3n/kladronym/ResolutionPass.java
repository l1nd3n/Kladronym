package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.name.ExtendedName;
import dev.l1nd3n.kladronym.name.NameMatch;
import dev.l1nd3n.kladronym.preprocess.Abbreviation;
import dev.l1nd3n.kladronym.preprocess.Token;
import dev.l1nd3n.kladronym.preprocess.Toponym;
import dev.l1nd3n.kladronym.kladr.KladrCode;

import java.util.List;
import java.util.Optional;

final class ResolutionPass {
    private final List<Token> tokens;
    private final KladrCatalog catalog;
    private final NameMatch nameMatch;
    private final ExtendedName extendedName;


    ResolutionPass(List<Token> tokens, KladrCatalog catalog, NameMatch nameMatch, ExtendedName extendedName) {
        this.tokens = tokens;
        this.catalog = catalog;
        this.nameMatch = nameMatch;
        this.extendedName = extendedName;
    }

    public Optional<Kladronym> resolve() {
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
                String extName = extendedName.extend(name, token.value());
                Toponym toponym = expectedType == null
                    ? new Toponym(extName)
                    : new Toponym(extName, expectedType);
                List<Kladronym> matched = catalog.find(toponym, scope, nameMatch);
                if (!matched.isEmpty()) {
                    name = extName;
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
