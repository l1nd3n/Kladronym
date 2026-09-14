package dev.l1nd3n.kladronym.name;

@FunctionalInterface
public interface ExtendedName {
    public String extend(String name, String candidate);
}
