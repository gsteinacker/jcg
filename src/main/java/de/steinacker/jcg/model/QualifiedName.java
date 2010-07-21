/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;


/**
 * The QualifiedName of a Type or Package.
 *
 * TODO: ist das eher ein TypeName? Ein 'normaler' QualifiedName sollte nichts über Wildcards etc. wissen müssen.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class QualifiedName implements CharSequence, Comparable<QualifiedName> {
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
        final String key = qualifiedName.toString();
        if (!INSTANCES.containsKey(key)) {
            if (qualifiedName instanceof QualifiedName)
                INSTANCES.put(key, (QualifiedName)qualifiedName);
            else
                INSTANCES.put(key, new QualifiedName(qualifiedName));
        }
        return INSTANCES.get(key);
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
        return SimpleName.valueOf(simpleName);
    }

    public String getPackage() {
        final int lastDot = qualifiedName.lastIndexOf(".");
        if (lastDot == -1)
            return "";
        else
            return qualifiedName.substring(0, lastDot);
    }

    /**
     * @return true if the named type is a primitive type, false otherwise.
     */
    public boolean isPrimitive() {
        return PRIMITIVE_TYPES.contains(qualifiedName);
    }

    /** Returns true if the type parameters is a type variable like <T>, false if it is a declared type like
     * java.lang.String.
     *
     * The current implementation assumes that all non-primitive types without a package declaration is a
     * type identifier.
     *
     * @return true if the QN is a type variable, false otherwise.
     * TODO: das sollte kein Member von QualifiedName sein!
     */
    public boolean isTypeVariable() {
        return getPackage().isEmpty() && !isPrimitive();
    }

    /**
     * Returns true if the QualifiedName is a wildcard identifier ('?').
     *
     * @return true if the QN is a wildcard.
     * TODO: das sollte kein Member von QualifiedName sein!
     */
    public boolean isWildcard() {
        return qualifiedName.equals("?");
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

    @Override
    public int compareTo(final QualifiedName o) {
        return qualifiedName.compareTo(o.qualifiedName);
    }
}
