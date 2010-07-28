/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Code analyzer model for storing details of annotation
 *
 * @author Guido Steinacker
 */
public final class Annotation {

    @NotNull
    @Valid
    private final QualifiedName name;
    @NotNull
    @Valid
    private final List<AnnotationParameter> parameters;
    @NotNull
    @Valid
    private final List<AnnotationParameter> defaults;

    public Annotation(final QualifiedName name) {
        this.name = name;
        this.parameters = Collections.emptyList();
        this.defaults = Collections.emptyList();
    }

    public Annotation(final QualifiedName name,
                      final List<AnnotationParameter> parameters,
                      final List<AnnotationParameter> defaults) {
        this.name = name;
        this.parameters = new ArrayList<AnnotationParameter>(parameters);
        this.defaults = new ArrayList<AnnotationParameter>(defaults);
    }

    public QualifiedName getName() {
        return name;
    }

    /**
     * Returns an unmodifiable list containing all AnnotationParameters of this Annotation.
     * If withDefaults is true, the list will also contain default parameters (that is, parameters
     * with a default value, which do neither occur in the parsed source code, nor in the
     * generated source code.
     * <p>
     * If there are no parameters, the returned list will be empty.
     * @param withDefaults specifies whether the results will contain default parameters.
     * @return unmodifiable list containing AnnotationParameters.
     */
    public List<AnnotationParameter> getParameters(final boolean withDefaults) {
        if (withDefaults)
            return Collections.unmodifiableList(defaults);
        else
            return Collections.unmodifiableList(parameters);
    }

    /**
     * Returns the AnnotationParameter with the specified name.
     * If withDefaults is true, also a default parameter will be returned (that is, a parameter
     * with a default value, which do neither occur in the parsed source code, nor in the
     * generated source code.
     * <p>
     * If the parameter does not exist, null is returned.
     * @param name the name of the requested parameter
     * @param withDefaults specifies whether the results will contain default parameters.
     * @return the requested AnnotationParameter or null, if this parameter does not exist.
     */
    public AnnotationParameter getParameter(final String name, final boolean withDefaults) {
        for (final AnnotationParameter parameter : (withDefaults ? defaults : parameters)) {
            if (parameter.getName().equals(name))
                return parameter;
        }
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Annotation that = (Annotation) o;

        if (!name.equals(that.name)) return false;
        if (!parameters.equals(that.parameters)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("@").append(name.getSimpleName());
        if (parameters.size() == 1) {
            sb.append('(');
            final AnnotationParameter valueParameter = getParameter("value", false);
            if(valueParameter != null) {
                // the prefix "value=" is not needed if it is the only parameter (value=42):
                String param = valueParameter.toString();
                if (valueParameter.getValues().size() == 1) {
                    // omit the value= and curly braces
                    param = param.substring("value={".length()-1);
                }
                sb.append(param);
            } else {
                // all other parameters must contain the parameter's name (foo=42):
                sb.append(parameters.get(0).toString());
            }
            sb.append(')');
        } else if (parameters.size() > 1) {
            // more than one parameter (value=42, foo=42):
            sb.append('(');
            final Iterator<AnnotationParameter> params = parameters.iterator();
            while (params.hasNext()) {
                final AnnotationParameter param = params.next();
                sb.append(param.toString());
                if (params.hasNext())
                    sb.append(", ");
            }
            sb.append(')');
        }
        return sb.toString();
    }
}