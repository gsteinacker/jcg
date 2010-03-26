/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public final class ParameterBuilder {
    @NotNull
    @Valid
    private QualifiedName typeName;
    @NotNull
    @Valid
    private SimpleName name;
    @NotNull
    @Valid
    private List<Annotation> annotations = Collections.emptyList();
    private boolean aFinal = true;
    @NotNull
    private String comment = "";

    public ParameterBuilder setTypeName(final QualifiedName typeName) {
        this.typeName = typeName;
        return this;
    }

    public ParameterBuilder setName(final SimpleName name) {
        this.name = name;
        return this;
    }

    public ParameterBuilder setAnnotations(final List<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    public ParameterBuilder setFinal(final boolean aFinal) {
        this.aFinal = aFinal;
        return this;
    }

    public ParameterBuilder setComment(final String comment) {
        this.comment = comment;
        return this;
    }

    public Parameter toParameter() {
        return new Parameter(typeName, name, annotations, aFinal, comment);
    }
}