/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;


/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class QualifiedName implements CharSequence {
    private static final Map<CharSequence, QualifiedName> INSTANCES
            = Collections.synchronizedMap(new HashMap<CharSequence, QualifiedName>());
    private static final Collection<String> PRIMITIVE_TYPES = Arrays.asList(
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "boolean",
            "char",
            "void"
    );

    static {
        for (final String primitiveType : PRIMITIVE_TYPES) {
            INSTANCES.put(primitiveType, new QualifiedName(primitiveType));
        }
    }

    @NotNull
    @Pattern(regexp = "[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]+)*")
    private final String qualifiedName;

    private QualifiedName(final CharSequence qualifiedName) {
        this.qualifiedName = qualifiedName.toString();
    }

    public static QualifiedName valueOf(final CharSequence qualifiedName) {
        if (qualifiedName == null)
            throw new NullPointerException("Parameter must not be null!");
        if (!INSTANCES.containsKey(qualifiedName)) {
            INSTANCES.put(qualifiedName, new QualifiedName(qualifiedName));
        }
        return INSTANCES.get(qualifiedName);
    }

    public static QualifiedName valueOf(final CharSequence packageName, final CharSequence simpleName) {
        if (packageName == null || simpleName == null)
            throw new NullPointerException("Parameters must not be null!");
        if (simpleName.length() > 0) {
            final CharSequence qualifiedName = packageName.length() > 0
                    ? packageName + "." + simpleName
                    : simpleName;
            return valueOf(qualifiedName);
        } else {
            return valueOf(packageName);
        }
    }

    public int length() {
        return qualifiedName.length();
    }

    public char charAt(final int index) {
        return qualifiedName.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return qualifiedName.subSequence(start, end);
    }

    public String toString() {
        return qualifiedName;
    }

    public SimpleName getSimpleName() {
        final String simpleName = qualifiedName.substring(
                1 + qualifiedName.lastIndexOf("."),
                qualifiedName.length());
        return new SimpleName(simpleName);
    }

    public String getPackage() {
        final int lastDot = qualifiedName.lastIndexOf(".");
        if (lastDot == -1)
            return "";
        else
            return qualifiedName.substring(0, lastDot);
    }

    public final boolean isPrimitive() {
        return PRIMITIVE_TYPES.contains(qualifiedName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final QualifiedName that = (QualifiedName) o;

        if (!qualifiedName.equals(that.qualifiedName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return qualifiedName.hashCode();
    }
}
