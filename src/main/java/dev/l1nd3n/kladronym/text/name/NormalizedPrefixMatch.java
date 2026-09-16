package dev.l1nd3n.kladronym.text.name;

public final class NormalizedPrefixMatch implements NameMatch {

  private static String compact(String value) {
    return value.replace(" ", "");
  }

  private static boolean endsAtWordBoundary(String candidate, int length) {
    int position = 0;
    for (String word : candidate.split(" ")) {
      position += word.length();
      if (position == length) {
        return true;
      }
      if (position > length) {
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean matches(String query, String candidate) {
    String normalizedQuery = new NormalizedString(query).get();
    String normalizedCandidate = new NormalizedString(candidate).get();
    String compactQuery = compact(normalizedQuery);
    return !compactQuery.isEmpty()
        && compact(normalizedCandidate).startsWith(compactQuery)
        && endsAtWordBoundary(normalizedCandidate, compactQuery.length());
  }
}
