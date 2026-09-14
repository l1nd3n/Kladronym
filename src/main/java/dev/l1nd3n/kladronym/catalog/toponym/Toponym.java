package dev.l1nd3n.kladronym.catalog.toponym;

public final class Toponym {
    private final String name;
    private final String type;

    public Toponym(String name) {
        this(name, "");
    }

    public Toponym(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public boolean matches(Toponym candidate, ToponymMatch match) {
        return match.matches(this, candidate);
    }

    @Override
    public String toString() {
        return type.isEmpty() ? name : "%s (%s)".formatted(name, type);
    }
}
