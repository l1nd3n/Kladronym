package dev.l1nd3n.kladronym.preprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LexemeAliases {
    private final Node root;

    private LexemeAliases(Node root) {
        this.root = root;
    }

    public List<String> replace(List<String> lexemes) {
        List<String> result = new ArrayList<>();
        int offset = 0;
        while (offset < lexemes.size()) {
            Replacement replacement = replacement(lexemes, offset);
            if (replacement == null) {
                result.add(lexemes.get(offset));
                offset++;
            } else {
                result.add(replacement.value());
                offset += replacement.length();
            }
        }
        return List.copyOf(result);
    }

    public static LexemeAliases create(Map<String, Abbreviation> abbreviations) {
        Node root = new Node(Map.of(), null);
        for (Map.Entry<String, Abbreviation> entry : abbreviations.entrySet()) {
            List<String> lexemes = lexemes(entry.getKey());
            root = add(root, lexemes, 0, entry.getValue().shortName());
        }
        return new LexemeAliases(root);
    }

    private Replacement replacement(List<String> lexemes, int offset) {
        Node node = root;
        String value = null;
        int length = 0;
        for (int index = offset; index < lexemes.size(); index++) {
            node = node.children().get(lexemes.get(index));
            if (node == null) break;
            if (node.value() != null) {
                value = node.value();
                length = index - offset + 1;
            }
        }
        if (value == null) return null;
        return new Replacement(value, length);
    }

    private static Node add(
        Node node,
        List<String> lexemes,
        int offset,
        String value
    ) {
        if (offset == lexemes.size()) return new Node(node.children(), value);
        String lexeme = lexemes.get(offset);
        Node found = node.children().get(lexeme);
        Node child = found == null ? new Node(Map.of(), null) : found;
        Node added = add(child, lexemes, offset + 1, value);
        Map<String, Node> children = new HashMap<>(node.children());
        children.put(lexeme, added);
        return new Node(Map.copyOf(children), node.value());
    }

    private static List<String> lexemes(String spelling) {
        if (spelling.isEmpty()) return List.of();
        return List.of(spelling.split(" "));
    }

    private static final class Node {
        private final Map<String, Node> children;
        private final String value;

        private Node(Map<String, Node> children, String value) {
            this.children = children;
            this.value = value;
        }

        Map<String, Node> children() {
            return children;
        }

        String value() {
            return value;
        }
    }

    private static final class Replacement {
        private final String value;
        private final int length;

        private Replacement(String value, int length) {
            this.value = value;
            this.length = length;
        }

        private String value() {
            return value;
        }

        private int length() {
            return length;
        }
    }
}
