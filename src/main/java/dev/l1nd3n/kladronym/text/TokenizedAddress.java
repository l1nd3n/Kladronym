package dev.l1nd3n.kladronym.text;

import java.util.ArrayList;
import java.util.List;

public final class TokenizedAddress {

  private final LexedAddress address;
  private final Aliases aliases;

  public TokenizedAddress(LexedAddress address, Aliases aliases) {
    this.address = address;
    this.aliases = aliases;
  }

  public List<Token> get() {
    List<String> lexemes = address.get();
    List<Token> tokens = new ArrayList<>();
    int offset = 0;
    while (offset < lexemes.size()) {
      TypeMatch match = aliases.match(lexemes, offset).orElse(null);
      if (match == null) {
        tokens.add(new Token(TokenType.TEXT, lexemes.get(offset)));
        offset++;
      } else {
        // Keep the canonical spelling: looking it up preserves the old ambiguities.
        tokens.add(new Token(TokenType.ABBREVIATION, match.spelling()));
        offset += match.length();
      }
    }
    return List.copyOf(tokens);
  }
}
