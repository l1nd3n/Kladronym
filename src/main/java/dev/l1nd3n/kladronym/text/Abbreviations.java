package dev.l1nd3n.kladronym.text;

import dev.l1nd3n.kladronym.text.name.NormalizedString;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class Abbreviations {

  private final Map<String, Set<String>> meanings;
  private final Map<String, String> spellings;

  public Abbreviations(Map<String, Set<String>> meanings) {
    this(meanings, meanings.keySet().stream()
        .collect(Collectors.toMap(spelling -> spelling, spelling -> spelling)));
  }

  public Abbreviations(Map<String, Set<String>> meanings, Map<String, String> spellings) {
    this.meanings = new LinkedHashMap<>();
    meanings.forEach((spelling, names) -> this.meanings.put(
        spelling, Collections.unmodifiableSet(new LinkedHashSet<>(names))));
    this.spellings = Map.copyOf(spellings);
  }

  public Optional<Set<String>> find(String spelling) {
    return Optional.ofNullable(meanings.get(new NormalizedString(spelling).get()));
  }

  public boolean compatible(String left, String right) {
    Set<String> leftNames = find(left).orElse(Set.of());
    Set<String> rightNames = find(right).orElse(Set.of());
    return leftNames.stream().anyMatch(rightNames::contains);
  }

  Map<String, String> spellings() {
    return spellings;
  }
}
