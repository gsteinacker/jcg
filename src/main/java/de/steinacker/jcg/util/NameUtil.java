/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.util;

import javax.validation.constraints.Pattern;
import java.util.*;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class NameUtil {

    @Pattern(regexp = "[\\s]*[a-zA-Z_]+[\\sa-zA-Z0-9_]*[\\sw]*")
    public static String toCamelHumpName(final String name, final boolean firstUpperCase) {
        final char[] chars = name.toCharArray();
        final StringBuilder camelHumpName = new StringBuilder();
        boolean nextUpperCase = false;
        boolean isFirstChar = true;
        for (final char c : chars) {
            if (isFirstChar) {
                if (Character.isWhitespace(c))
                    continue;
                if (c == '_') {
                    camelHumpName.append(c);
                    continue;
                }
                if (firstUpperCase) {
                    camelHumpName.append(Character.toUpperCase(c));
                } else {
                    camelHumpName.append(Character.toLowerCase(c));
                }
                isFirstChar = false;
            } else {
                if (Character.isWhitespace(c)) {
                    nextUpperCase = true;
                    continue;
                }
                if (nextUpperCase) {
                    camelHumpName.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    camelHumpName.append(c);
                }
            }
        }
        return camelHumpName.toString();
    }

    /**
     * Returns true, if the first letter of the name is upper case.
     * All leading non-letter characters are ignored.
     *
     * @param text the text
     * @return boolean
     */
    public static boolean isFirstUpperCase(final CharSequence text) {
        for (int i = 0, n = text.length(); i < n; ++i) {
            if (Character.isLetter(text.charAt(i)))
                return Character.isUpperCase(text.charAt(i));
        }
        return false;
    }

    public static CharSequence toFirstLowerCase(final CharSequence name) {
        if (!isFirstUpperCase(name))
            return name;
        if (name.length() == 1) {
            return name.toString().toLowerCase();
        } else {
            return new StringBuilder()
                    .append(Character.toLowerCase(name.charAt(0)))
                    .append(name.subSequence(1, name.length()));
        }
    }

    public static CharSequence toFirstUpperCase(final CharSequence name) {
        if (isFirstUpperCase(name))
            return name;
        if (name.length() == 1) {
            return name.toString().toUpperCase();
        } else {
            return new StringBuilder()
                    .append(Character.toUpperCase(name.charAt(0)))
                    .append(name.subSequence(1, name.length()));
        }
    }

    /** "AAA" => {"AAA"}
     *  "aaa" => {"aaa"}
     *  "AaAa" => {"Aa", "Aa"}
     *  "AAAa" => {"AA", "Aa"}
     */
    public static String[] splitCamelHumpName(final String name) {
        final List<String> strings = new LinkedList<String>();
        final char[] chars = name.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i=0,n=chars.length; i<n; ++i) {
            final char c = chars[i];
            if (Character.isUpperCase(c)) {
                strings.add(sb.toString());
                sb = new StringBuilder().append(c);
            } else {
                sb.append(c);
            }
        }
        strings.add(sb.toString());
        final Iterator<String> iter = strings.iterator();
        while (iter.hasNext()) {
            final String s = iter.next();
            if (s.isEmpty())
                iter.remove();
        }
        // TODO: "K", "F", "Z" wieder zusammenfassen!
        return strings.toArray(new String[strings.size()]);
    }

}
