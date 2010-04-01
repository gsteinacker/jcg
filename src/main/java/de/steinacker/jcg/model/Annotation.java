/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Code analyzer model for storing details of annotation
 *
 * @author Guido Steinacker
 */
public final class Annotation {

    private static final String VALUE_PARAM = "value";

    @NotNull
    @Valid
    private final QualifiedName name;
    @NotNull
    private final Map<String, Object> parameters = new LinkedHashMap<String, Object>();
    // TODO Annotations sind etwas komplizierter; es werden nich nur String Werte zugelassen.

    public Annotation(final QualifiedName name) {
        this.name = name;
    }

    public Annotation(final QualifiedName name, final Object value) {
        this.name = name;
        this.parameters.put(VALUE_PARAM, value);
    }

    public Annotation(final QualifiedName name, final Map<String, ?> parameters) {
        this.name = name;
        this.parameters.putAll(parameters);
    }

    public QualifiedName getName() {
        return name;
    }

    public Object getValue() {
        return parameters.containsKey(VALUE_PARAM) ? parameters.get(VALUE_PARAM) : "";
    }

    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public Object getParameter(final String name) {
        return parameters.get(name);
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
        if (parameters.size() == 1 && parameters.containsKey(VALUE_PARAM))
            sb.append("(\"").append(parameters.get(VALUE_PARAM)).append("\")");
        if (parameters.size() > 1) {
            sb.append('(');
            boolean first = true;
            for (final Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(entry.getKey()).append("=\"").append(entry.getValue()).append('"');
            }
            sb.append(')');
        }
        return sb.toString();
    }
}