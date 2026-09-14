package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.Kladronym;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.catalog.toponym.ToponymMatch;
import dev.l1nd3n.kladronym.text.Token;
import dev.l1nd3n.kladronym.text.name.ExtendedName;

import java.util.List;
import java.util.Optional;

final class ResolutionPass {
    private final List<Token> tokens;
    private final Kladr catalog;
    private final ToponymMatch toponymMatch;
    private final ExtendedName extendedName;


    ResolutionPass(List<Token> tokens, Kladr catalog, ToponymMatch toponymMatch, ExtendedName extendedName) {
        this.tokens = tokens;
        this.catalog = catalog;
        this.toponymMatch = toponymMatch;
        this.extendedName = extendedName;
    }

    public Optional<Kladronym> resolve() {
        int index = 0;
        KladrCode scope = KladrCode.ROOT;
        Kladronym result = null;
        String expectedType = null;
        String name = "";
        List<Kladronym> candidates = List.of();
        while (index < tokens.size()) {
            Token token = tokens.get(index);
            if (token.isAbbreviation()) {
                if (candidates.isEmpty()) {
                    expectedType = token.value();
                } else if (expectedType != null) {
                    result = candidates.getFirst();
                    scope = result.code();
                    expectedType = token.value();
                    name = "";
                    candidates = List.of();
                } else {
                    List<Kladronym> typed = catalog.find(
                            new Toponym(name, token.value()),
                            scope,
                            toponymMatch
                    );
                    if (typed.isEmpty()) {
                        result = candidates.getFirst();
                        expectedType = token.value();
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
                List<Kladronym> matched = catalog.find(toponym, scope, toponymMatch);
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
