package dev.l1nd3n.kladronym;

@FunctionalInterface
public interface ToponymNameMatch {
    boolean matches(String query, String candidate);
}
