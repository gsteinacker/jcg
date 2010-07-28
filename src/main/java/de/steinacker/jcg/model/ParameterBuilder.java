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
    private TypeSymbol type;
    @NotNull
    @Valid
    private SimpleName name;
    @NotNull
    @Valid
    private List<Annotation> annotations = Collections.emptyList();
    private boolean isFinal = false;
    @NotNull
    private String comment = "";

    public ParameterBuilder() {
    }

    public ParameterBuilder(final Parameter prototype) {
        this.type = prototype.getType();
        this.name = prototype.getName();
        this.annotations = prototype.getAnnotations();
        this.isFinal = prototype.isFinal();
        this.comment = prototype.getComment();
    }

    public ParameterBuilder setType(final TypeSymbol typeSymbol) {
        this.type = typeSymbol;
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
        this.isFinal = aFinal;
        return this;
    }

    public ParameterBuilder setComment(final String comment) {
        this.comment = comment;
        return this;
    }

    public Parameter toParameter() {
        return new Parameter(type, name, annotations, isFinal, comment);
    }
}