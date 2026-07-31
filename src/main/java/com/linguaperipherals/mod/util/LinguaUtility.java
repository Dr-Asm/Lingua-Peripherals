package com.linguaperipherals.mod.util;

import java.nio.charset.StandardCharsets;

/**
 * Utility methods for handling strings that cross the CC Lua → Java boundary.
 *
 * CC:Tweaked's Cobalt Lua engine converts Lua strings to Java Strings using
 * a single-byte-per-character mapping (equivalent to ISO-8859-1). Multi-byte
 * UTF-8 sequences are broken into individual garbled characters.
 *
 * {@link #fixLuaString(String)} reverses this mapping and re-decodes the
 * result as UTF-8, recovering the original string. It also handles legacy
 * {@code \\uXXXX}-escaped strings as a fallback.
 */
public final class LinguaUtility {
    private LinguaUtility() {}

    /**
     * Fix a String received from CC Lua through a {@code @LuaFunction} parameter.
     *
     * <p>This performs two steps:
     * <ol>
     *   <li>Reverse Cobalt's byte-to-char mapping ({@code (char)(byte & 0xFF)})
     *       and re-decode as UTF-8, recovering strings written with Lua's
     * {@code \\uXXXX} escapes.</li>
     *   <li>Apply legacy {@code \\uXXXX} escape decoding as a fallback for
     *       pre-existing code that used the double-backslash workaround.</li>
     * </ol>
     *
     * <p>This method is safe to call on any CC string — ASCII strings pass
     * through unchanged in both steps.
     *
     * @param raw The raw String received from CC Lua
     * @return The corrected string
     */
    public static String fixLuaString(String raw) {
        if (raw == null || raw.isEmpty()) return raw;

        // Step 1: Reverse Cobalt's byte→char mapping
        byte[] bytes = new byte[raw.length()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) raw.charAt(i);
        }
        String decoded = new String(bytes, StandardCharsets.UTF_8);

        // Step 2: Backward compat — handle legacy \\uXXXX escapes
        return TextUtils.decodeEscapeSequences(decoded);
    }

    /**
     * Encode a Java String as UTF-8 bytes for safe return to CC Lua.
     *
     * <p>CC's Cobalt engine passes {@code byte[]} returns directly to Lua
     * as raw bytes, bypassing its lossy character encoding.  Use this for
     * any read method that should return non-ASCII text intact.</p>
     *
     * @param s The Java string
     * @return UTF-8-encoded bytes
     */
    public static byte[] toLuaBytes(String s) {
        if (s == null || s.isEmpty()) return new byte[0];
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
