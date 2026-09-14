package dev.l1nd3n.kladronym.name;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ReversedNameMatch implements NameMatch {
    private final NameMatch origin;

    public ReversedNameMatch(NameMatch origin) {
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
