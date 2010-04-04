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
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class Parameter implements Annotatable {
    @NotNull
    @Valid
    private final QualifiedName typeName;
    @NotNull
    @Valid
    private final SimpleName name;
    @NotNull
    @Valid
    private final List<Annotation> annotations;
    private final boolean isFinal;
    private final String comment;


    public Parameter(final QualifiedName typeName,
                     final SimpleName name,
                     final List<Annotation> annotations,
                     final boolean isFinal,
                     final String comment) {
        this.typeName = typeName;
        this.name = name;
        this.annotations = Collections.unmodifiableList(new ArrayList<Annotation>(annotations));
        this.isFinal = isFinal;
        this.comment = comment;
    }

    public QualifiedName getTypeName() {
        return typeName;
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
        if (!typeName.equals(parameter.typeName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = typeName.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + annotations.hashCode();
        result = 31 * result + (isFinal ? 1 : 0);
        result = 31 * result + comment.hashCode();
        return result;
    }
}
