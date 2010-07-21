/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a method parameter.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class Parameter implements Annotatable {
    /** The TypeSymbol representing the parameter's type. */
    @NotNull
    @Valid
    private final TypeSymbol type;
    /** The name of the parameter. */
    @NotNull
    @Valid
    private final SimpleName name;
    /** A list of annotations of the parameter. */
    @NotNull
    @Valid
    private final List<Annotation> annotations;
    /** Flag indicating whether the parameter is final or not. */
    private final boolean isFinal;
    /** An optional comment of the parameter. */
    private final String comment;

    // TODO: prüfen, ob ich den comment benötige!

    public Parameter(final TypeSymbol type,
                     final SimpleName name,
                     final List<Annotation> annotations,
                     final boolean isFinal,
                     final String comment) {
        this.type = type;
        this.name = name;
        this.annotations = Collections.unmodifiableList(new ArrayList<Annotation>(annotations));
        this.isFinal = isFinal;
        this.comment = comment;
    }

    public TypeSymbol getType() {
        return type;
    }

    public SimpleName getName() {
        return name;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Parameter parameter = (Parameter) o;

        if (isFinal != parameter.isFinal) return false;
        if (!annotations.equals(parameter.annotations)) return false;
        if (!comment.equals(parameter.comment)) return false;
        if (!name.equals(parameter.name)) return false;
        if (!type.equals(parameter.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + annotations.hashCode();
        result = 31 * result + (isFinal ? 1 : 0);
        result = 31 * result + comment.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (!annotations.isEmpty()) {
            boolean first = true;
            for (final Annotation annotation : annotations) {
                if (!first) {
                    sb.append(", ");
                    first = false;
                }
                sb.append(annotation.toString());
            }
            sb.append(" ");
        }
        return sb.append(isFinal() ? "final " : "")
                .append(getType().toString().toString())
                .append(" ")
                .append(getName().toString())
                .toString();
    }
}
