package dev.l1nd3n.kladronym.catalog;

import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;

public final class Kladronym implements Comparable<Kladronym> {

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
  public int compareTo(Kladronym other) {
    int byRank = code.rank().compareTo(other.code.rank());
      if (byRank != 0) {
          return byRank;
      }
    return code.toString().compareTo(other.code.toString());
  }
}
