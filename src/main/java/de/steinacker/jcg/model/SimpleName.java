/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * A simple name with support of camel-hump names.
 * Valid names must match to [a-zA-Z_][a-zA-Z_0-9]*
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class SimpleName implements CharSequence, Comparable<SimpleName> {
    @NotNull
    @Pattern(regexp = "[a-zA-Z_][a-zA-Z_0-9]*")
    private String name;

    public SimpleName(final CharSequence name) {
        this.name = name.toString();
    }

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SimpleName that = (SimpleName) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(final SimpleName o) {
        return name.compareTo(o.name);
    }
}
