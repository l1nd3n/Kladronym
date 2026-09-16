package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.catalog.toponym.ToponymMatch;
import dev.l1nd3n.kladronym.text.Abbreviations;
import dev.l1nd3n.kladronym.text.name.NameMatch;

public final class StandardToponymMatch implements ToponymMatch {

  private final NameMatch names;
  private final Abbreviations abbreviations;

  public StandardToponymMatch(NameMatch names, Abbreviations abbreviations) {
    this.names = names;
    this.abbreviations = abbreviations;
  }

  @Override
  public boolean matches(Toponym query, Toponym candidate) {
    return names.matches(query.name(), candidate.name())
        && (query.type().isEmpty() || candidate.type().isEmpty()
        || abbreviations.compatible(query.type(), candidate.type()));
  }
}
