package dev.l1nd3n.kladronym.catalog;

import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;

public final class Kladronym {

  private final Toponym toponym;
  private final KladrCode code;

  public Kladronym(Toponym toponym, KladrCode code) {
    this.toponym = toponym;
    this.code = code;
  }

  public Toponym toponym() {
    return toponym;
  }

  public KladrCode code() {
    return code;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Kladronym other)) {
      return false;
    }
    return code.equals(other.code);
  }

  @Override
  public int hashCode() {
    return code.hashCode();
  }
}