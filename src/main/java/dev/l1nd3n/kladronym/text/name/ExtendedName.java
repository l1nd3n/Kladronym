package dev.l1nd3n.kladronym.text.name;

@FunctionalInterface
public interface ExtendedName {

  String extend(String name, String candidate);
}
