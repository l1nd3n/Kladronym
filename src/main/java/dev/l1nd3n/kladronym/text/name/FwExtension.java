package dev.l1nd3n.kladronym.text.name;

public class FwExtension implements ExtendedName {

  public String extend(String name, String candidate) {
    return name.isEmpty() ? candidate : name + " " + candidate;
  }
}
