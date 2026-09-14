package dev.l1nd3n.kladronym.text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Aliases {
    private final Abbreviations abbreviations;
    private volatile Node root;

    public Aliases(Abbreviations abbreviations) {
        this.abbreviations = abbreviations;
    }

    /**
     * Matches normalized lexemes, preferring the longest spelling.
     */
    public Optional<TypeMatch> match(List<String> lexemes, int offset) {
        Node node = root();
        TypeMatch result = null;
        for (int index = offset; index < lexemes.size(); index++) {
            node = node.children().get(lexemes.get(index));
            if (node == null) break;
            if (node.spelling() != null) {
                result = new TypeMatch(node.spelling(), index - offset + 1);
            }
        }
        return Optional.ofNullable(result);
    }

    private Node root() {
        Node found = root;
        if (found == null) {
            synchronized (this) {
                found = root;
                if (found == null) {
                    found = build();
                    root = found;
                }
            }
        }
        return found;
    }

    private Node build() {
        Node tree = new Node(Map.of(), null);
        for (var entry : abbreviations.spellings().entrySet()) {
            if (!entry.getKey().isEmpty()) {
                tree = add(tree, List.of(entry.getKey().split(" ")), 0, entry.getValue());
            }
        }
        return tree;
    }

    private Node add(Node node, List<String> lexemes, int offset, String spelling) {
        if (offset == lexemes.size()) return new Node(node.children(), spelling);
        String lexeme = lexemes.get(offset);
        Node child = node.children().getOrDefault(lexeme, new Node(Map.of(), null));
        Map<String, Node> children = new LinkedHashMap<>(node.children());
        children.put(lexeme, add(child, lexemes, offset + 1, spelling));
        return new Node(Map.copyOf(children), node.spelling());
    }

    private record Node(Map<String, Node> children, String spelling) {
    }
}
