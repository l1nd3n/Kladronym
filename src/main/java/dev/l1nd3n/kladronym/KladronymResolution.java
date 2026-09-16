package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.Kladronym;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.catalog.toponym.ToponymMatch;
import dev.l1nd3n.kladronym.text.Token;
import dev.l1nd3n.kladronym.text.TokenType;
import dev.l1nd3n.kladronym.text.name.ExtendedName;

import java.util.List;
import java.util.Optional;

final class KladronymResolution {
    private final List<Token> tokens;
    private final Kladr catalog;
    private final ToponymMatch toponymMatch;
    private final ExtendedName extendedName;


    KladronymResolution(List<Token> tokens, Kladr catalog, ToponymMatch toponymMatch, ExtendedName extendedName) {
        this.tokens = List.copyOf(tokens);
        this.catalog = catalog;
        this.toponymMatch = toponymMatch;
        this.extendedName = extendedName;
    }

    Optional<Kladronym> resolve() {
        ResolutionState state = new ResolutionState("", null, KladrCode.ROOT);
        for (Token token : tokens) {
            state = state.update(token);
        }
        return state.getResult();
    }

    private class ResolutionState {
        private final String name;
        private final String expectedType;
        private final KladrCode scope;

        ResolutionState(String name, String expectedType, KladrCode currScope) {
            this.name = name;
            this.expectedType = expectedType;
            this.scope = currScope;
        }

        Optional<Kladronym> getResult() {
            List<Kladronym> candidates = candidates();
            if (!candidates.isEmpty()) {
                return Optional.of(candidates.getFirst());
            }
            return scope.equals(KladrCode.ROOT)
                    ? Optional.empty()
                    : catalog.find(scope);
        }

        ResolutionState update(Token token) {
            List<Kladronym> candidates = candidates();
            return token.type() == TokenType.ABBREVIATION
                    ? updAsAbbreviation(token, candidates)
                    : updAsText(token, candidates);
        }

        private List<Kladronym> candidates() {
            return name.isEmpty()
                    ? List.of()
                    : matches(name, expectedType, scope);
        }

        private List<Kladronym> matches(String name, String expectedType, KladrCode scope) {
            Toponym suspected = expectedType == null ? new Toponym(name) : new Toponym(name, expectedType);
            return catalog.find(suspected, scope, toponymMatch);
        }

        private ResolutionState updAsAbbreviation(Token token, List<Kladronym> candidatesI) {
            String type = token.value();
            if (candidatesI.isEmpty()) {
                return new ResolutionState(name, type, scope);
            }
            if (expectedType != null) {
                return new ResolutionState("", type, candidatesI.getFirst().code());
            }
            List<Kladronym> typed = matches(name, type, scope);
            if (!typed.isEmpty()) {
                return new ResolutionState("", null, typed.getFirst().code());
            }
            return new ResolutionState("", type, candidatesI.getFirst().code());
        }

        private ResolutionState updAsText(
                Token token,
                List<Kladronym> candidates
        ) {
            String extName = extendedName.extend(name, token.value());
            List<Kladronym> matched = matches(extName, expectedType, scope);
            if (!matched.isEmpty()) {
                return new ResolutionState(extName, expectedType, scope);
            }
            if (candidates.isEmpty()) {
                return new ResolutionState(name, null, scope);
            }
            KladrCode nextScope = candidates.getFirst().code();
            String nextName = extendedName.extend("", token.value());
            List<Kladronym> retried = matches(nextName, null, nextScope);
            return new ResolutionState(
                    retried.isEmpty() ? "" : nextName,
                    null,
                    nextScope
            );
        }
    }
}
