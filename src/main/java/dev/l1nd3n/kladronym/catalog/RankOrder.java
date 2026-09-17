package dev.l1nd3n.kladronym.catalog;

import java.util.Comparator;

public final class RankOrder implements Comparator<Kladronym> {

  @Override
  public int compare(Kladronym first, Kladronym second) {
    int byRank = first.code().rank().compareTo(second.code().rank());
    if (byRank != 0) {
      return byRank;
    }
    return first.code().toString().compareTo(second.code().toString());
  }
}
