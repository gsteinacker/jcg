/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Represents an import statement.
 *
 * @author Guido Steinacker
 * @since 22.07.2010
 */
public class Import {
    @NotNull
    @Valid
    // TODO: @ScriptAssert: keine primitive, type-var oder wildcard
    private final QualifiedName qualifiedName;
    private final boolean isStatic;

    /**
     * Creates a new Import instance.
     * @param qualifiedName the QualifiedName of the imported type.
     * @param isStatic static or non-static import.
     *
     */
    public Import(final QualifiedName qualifiedName, final boolean isStatic) {
        if (qualifiedName.isPrimitive() || qualifiedName.isTypeVariable() || qualifiedName.isWildcard())
            throw new IllegalArgumentException("QualifiedName must not be a primitive, a type variable or a wildcard.");
        this.qualifiedName = qualifiedName;
        this.isStatic = isStatic;
    }

    /**
     * creates a non-static Import instance.
     * @param qualifiedName the QualifiedName of the imported type.
     */
    public Import(final QualifiedName qualifiedName) {
        if (qualifiedName.isPrimitive() || qualifiedName.isTypeVariable() || qualifiedName.isWildcard())
            throw new IllegalArgumentException("QualifiedName must not be a primitive, a type variable or a wildcard.");
        this.qualifiedName = qualifiedName;
        this.isStatic = false;
    }

    /**
     * Returns the QualifiedName representing the imported type(s).
     * @return QualifiedName
     */
    public QualifiedName getQualifiedName() {
        return qualifiedName;
    }

    /**
     * Returns the static flag, indicating whether the import ist a static import.
     * @return true if the import ist static, false otherwise.
     */
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Import anImport = (Import) o;

        if (isStatic != anImport.isStatic) return false;
        if (!qualifiedName.equals(anImport.qualifiedName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qualifiedName.hashCode();
        result = 31 * result + (isStatic ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("import ")
                .append(isStatic() ? "static " : "")
                .append(qualifiedName)
                .append(';')
                .toString();
    }
}
