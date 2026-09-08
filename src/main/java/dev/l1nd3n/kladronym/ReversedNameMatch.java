package dev.l1nd3n.kladronym;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ReversedNameMatch implements ToponymNameMatch {
    private final ToponymNameMatch origin;

    ReversedNameMatch(ToponymNameMatch origin) {
        this.origin = origin;
    }

    @Override
    public boolean matches(String query,String candidate) {
        return origin.matches(reverse(query),reverse(candidate));
    }

    private static String reverse(String value) {
        List<String> words = new ArrayList<>(List.of(value.split(" ")));
        Collections.reverse(words);
        return String.join(" ",words);
    }
}
