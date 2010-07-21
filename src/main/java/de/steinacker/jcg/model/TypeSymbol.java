/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TypeSymbols are used to specify a method's return type, parameter type, and so on.
 *
 * Examples for a TypeSymbol:
 * <ul>
 * <li>
 * <code>
 * java.util.List&lt;? extends Number>: TypeSymbol with java.util.List as the QualifiedName and
 * a single wildcard-TypeParameter '?' extending java.util.Number as the bounded type.
 * </code>
 * </li>
 * <li>
 * java.util.List<&lt;? super T> TODO: List<? super T> - wie wird das abgebildet?!? QN=List, param=(WILDCARD, super, TYPEVARIABLE)
 * </li>
 * <li>
 * T: TypeSymbol with T as a TypeVariable and java.lang.Object as the bounded type.
 * </li>
 * </ul>
 *
 *
 */
public final class TypeSymbol {

    @NotNull
    @Valid
    private final QualifiedName qualifiedName;
    @NotNull
    @Valid
    private final List<TypeParameter> typeParameters;

    /**
     * Creates a new TypeSymbol without any TypeParameters.
     * @param qualifiedName the QualifiedName of the Type.
     */
    public TypeSymbol(final QualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.typeParameters = Collections.emptyList();
    }

    /**
     * Creates a new TypeSymbol with zero or more TypeParameters.
     * @param qualifiedName the QualifiedName of the Type
     * @param typeParameters the list of TypeParameters.
     */
    public TypeSymbol(final QualifiedName qualifiedName, final List<TypeParameter> typeParameters) {
        this.qualifiedName = qualifiedName;
        this.typeParameters = Collections.unmodifiableList(new ArrayList<TypeParameter>(typeParameters));
    }

    /**
     * Returns the fully qualified name of the TypeSymbol.
     * @return QualifiedName
     */
    public QualifiedName getQualifiedName() {
        return qualifiedName;
    }

    /**
     * Returns the TypeParameters of the TypeSymbol, or an empty List, if the TypeSymbol is not parameterized.
     * @return the list of type parameters.
     */
    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    /**
     * Returns true if the TypeSymbol is parameterized.
     *
     * @return true if there are any type parameters, false otherwise.
     */
    public boolean isParameterized() {
        return typeParameters.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeSymbol typeSymbol = (TypeSymbol) o;

        if (!qualifiedName.equals(typeSymbol.qualifiedName)) return false;
        if (!typeParameters.equals(typeSymbol.typeParameters)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qualifiedName.hashCode();
        result = 31 * result + typeParameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(qualifiedName.getSimpleName());
        if (!typeParameters.isEmpty()) {
            sb.append("<");
            boolean firstParam = true;
            for (final TypeParameter param : typeParameters) {
                if (firstParam)
                    firstParam = false;
                else
                    sb.append(", ");
                sb.append(param.toString());
            }
            sb.append(">");
        }
        return sb.toString();
    }
}
