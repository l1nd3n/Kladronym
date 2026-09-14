package dev.l1nd3n.kladronym.name;

@FunctionalInterface
public interface NameMatch {
    boolean matches(String query, String candidate);
}
