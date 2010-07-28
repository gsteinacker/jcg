/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The value of an AnnotationParameter.
 * A value is:
 * <ul>
 * <li>List&gt;AnnotationValue> if parameter value is an array (@foo (bar={1, 2, 3})
 * <li>Annotation if the parameter value is an Annotation (@foo(bar=@Bar))
 * <li>QualifiedName if the parameter value is a type (@foo (bar=String.class))
 * <li>String if the parameter value is a String
 * <li>Wrapper (like Integer), if the parameter is a primitive type
 * </ul>
 */
public final class AnnotationValue {
    /** The parameter value. */
    @NotNull
    private final Object value;
    /** A String representation of the parameter value.
     * This may be used to generate source code.
     */
    @NotNull
    @Size(min = 1)
    private final String stringValue;

    /**
     * Creates an AnnotationValue.
     *
     * @param value The parameter value.
     * @param stringValue A String representation of the parameter value. This may be used to generate source code.
     */
    public AnnotationValue(final Object value, final String stringValue) {
        this.value = value;
        this.stringValue = stringValue;
    }

    /**
     * Returns the value of the parameter.
     *
     * @return Object
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns a String representation of the parameter's value, usable to
     * generate source code.
     * @return String
     */
    public String getValueAsString() {
        return stringValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AnnotationValue that = (AnnotationValue) o;

        if (!stringValue.equals(that.stringValue)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + stringValue.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
