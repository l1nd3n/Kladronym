package dev.l1nd3n.kladronym.text.name;

@FunctionalInterface
public interface NameMatch {
    boolean matches(String query, String candidate);
}
