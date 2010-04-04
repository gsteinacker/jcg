/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A parameter of an Annotation.
 */
public final class AnnotationParameter {
    /** The parameter name. */
    @NotNull
    @Size(min = 1)
    private final String name;
    /** The parameter values. */
    @NotNull
    @Valid
    private final List<AnnotationValue> value;
    private final boolean defaultValued;

    public AnnotationParameter(final String name, final boolean defaultValued, final AnnotationValue... value) {
        this.name = name;
        this.defaultValued = defaultValued;
        this.value = Arrays.asList(value);
    }

    public AnnotationParameter(final String name, final boolean defaultValued, final List<AnnotationValue> value) {
        this.name = name;
        this.defaultValued = defaultValued;
        this.value = new ArrayList<AnnotationValue>(value);
    }

    /** Returns the name of the parameter.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    public boolean isDefaultValued() {
        return defaultValued;
    }

    /**
     * Returns the value of the parameter.
     *
     * @return Object
     */
    public List<AnnotationValue> getValues() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AnnotationParameter that = (AnnotationParameter) o;

        if (defaultValued != that.defaultValued) return false;
        if (!name.equals(that.name)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + (defaultValued ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(name).append('=');
        if (value.size() == 1) {
            sb.append(value.get(0).getValueAsString());
        } else {
            sb.append('{');
            final Iterator<AnnotationValue> iter = value.iterator();
            while (iter.hasNext()) {
                sb.append(iter.next());
                if (iter.hasNext())
                    sb.append(", ");
            }
            sb.append('}');
        }
        return sb.toString();
    }
}
