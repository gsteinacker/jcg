/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class MethodBuilder {
    private SimpleName name;
    private String comment;
    private List<Annotation> annotations;
    private Set<MethodModifier> modifiers;
    private List<QualifiedName> exceptions;
    private QualifiedName returnTypeName;
    private List<Parameter> parameters;
    private String methodBody;

    public MethodBuilder() {
        name = null;
        comment = "";
        annotations = new ArrayList<Annotation>();
        modifiers = EnumSet.noneOf(MethodModifier.class);
        exceptions = new ArrayList<QualifiedName>();
        returnTypeName = QualifiedName.valueOf("void");
        parameters = new ArrayList<Parameter>();
        methodBody = "";
    }

    public MethodBuilder(final Method prototype) {
        this.name = prototype.getName();
        this.comment = prototype.getComment();
        this.annotations = new ArrayList<Annotation>(prototype.getAnnotations());
        this.modifiers = EnumSet.copyOf(prototype.getModifiers());
        this.exceptions = new ArrayList<QualifiedName>(prototype.getExceptions());
        this.returnTypeName = prototype.getReturnTypeName();
        this.parameters = new ArrayList<Parameter>(prototype.getParameters());
        this.methodBody = prototype.getMethodBody();
    }

    public MethodBuilder setName(final SimpleName name) {
        this.name = name;
        return this;
    }

    public MethodBuilder setComment(final String comment) {
        this.comment = comment;
        return this;
    }

    public MethodBuilder setAnnotations(final List<Annotation> annotations) {
        this.annotations = new ArrayList<Annotation>(annotations);
        return this;
    }

    public MethodBuilder addAnnotation(final Annotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public MethodBuilder setModifiers(final Set<MethodModifier> modifiers) {
        this.modifiers = EnumSet.copyOf(modifiers);
        return this;
    }

    public MethodBuilder addModifier(final MethodModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public MethodBuilder setExceptions(final List<QualifiedName> exceptions) {
        this.exceptions = new ArrayList<QualifiedName>(exceptions);
        return this;
    }

    public MethodBuilder addException(final QualifiedName exception) {
        this.exceptions.add(exception);
        return this;
    }

    public MethodBuilder setReturnTypeName(final QualifiedName returnTypeName) {
        this.returnTypeName = returnTypeName;
        return this;
    }

    public MethodBuilder setParameters(final List<Parameter> parameters) {
        this.parameters = new ArrayList<Parameter>(parameters);
        return this;
    }

    public MethodBuilder addParameter(final Parameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public MethodBuilder setMethodBody(final String methodBody) {
        this.methodBody = methodBody;
        return this;
    }

    public Method toMethod() {
        return new Method(name, annotations, modifiers, exceptions, returnTypeName, parameters, comment, methodBody);
    }
}