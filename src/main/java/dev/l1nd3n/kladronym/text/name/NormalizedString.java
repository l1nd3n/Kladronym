package dev.l1nd3n.kladronym.text.name;

public final class NormalizedString {
    private final String source;

    public NormalizedString(String source) {
        this.source = source;
    }

    public String get() {
        StringBuilder result = new StringBuilder(source.length());
        boolean separator = true;
        for (int offset = 0; offset < source.length(); ) {
            int character = source.codePointAt(offset);
            offset += Character.charCount(character);
            if (character == '-') continue;
            if (Character.isLetterOrDigit(character)) {
                int lowercase = Character.toLowerCase(character);
                result.appendCodePoint(lowercase == 'ё' ? 'е' : lowercase);
                separator = false;
            } else if (!separator) {
                result.append(' ');
                separator = true;
            }
        }
        if (!result.isEmpty() && result.charAt(result.length() - 1) == ' ') {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }
}
