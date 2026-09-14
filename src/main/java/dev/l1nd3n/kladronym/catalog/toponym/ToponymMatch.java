package dev.l1nd3n.kladronym.catalog.toponym;

@FunctionalInterface
public interface ToponymMatch {
    boolean matches(Toponym query, Toponym candidate);
}
