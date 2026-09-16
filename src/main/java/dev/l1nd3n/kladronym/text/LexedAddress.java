package dev.l1nd3n.kladronym.text;

import dev.l1nd3n.kladronym.text.name.NormalizedString;
import java.util.List;

public final class LexedAddress {

  private final NormalizedString address;

  public LexedAddress(NormalizedString address) {
    this.address = address;
  }

  public List<String> get() {
    String normalized = address.get();
    if (normalized.isEmpty()) {
      return List.of();
    }
    return List.of(normalized.split(" "));
  }
}
