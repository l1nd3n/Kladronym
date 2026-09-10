package dev.l1nd3n.kladronym;

final class ExtendedName {
    private final String name;
    private final String word;

    ExtendedName(String name, String word) {
        this.name = name;
        this.word = word;
    }

    String frontwards() {
        return name.isEmpty() ? word : name + " " + word;
    }

    String backwards() {
        return name.isEmpty() ? word : word + " " + name;
    }
}
