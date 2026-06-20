package com.linguaperipherals.mod.util;

/**
 * Shared text utilities for display peripherals.
 */
public final class TextUtils {
    private TextUtils() {}

    public static String decodeEscapeSequences(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '\\' && i + 5 < text.length() && text.charAt(i + 1) == 'u') {
                String hex = text.substring(i + 2, i + 6);
                try { result.append((char) Integer.parseInt(hex, 16)); i += 6; continue; }
                catch (NumberFormatException ignored) {}
            }
            result.append(text.charAt(i));
            i++;
        }
        return result.toString();
    }

    public static String encodeNonAscii(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c > 127) sb.append(String.format("\\u%04x", (int) c));
            else sb.append(c);
        }
        return sb.toString();
    }
}