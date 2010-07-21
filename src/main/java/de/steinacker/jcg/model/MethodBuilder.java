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
    private Method.Kind kind;
    private String comment;
    private List<Annotation> annotations;
    private Set<MethodModifier> modifiers;
    private List<QualifiedName> exceptions;
    private List<TypeParameter> typeParameters;
    private TypeSymbol returnType;
    private List<Parameter> parameters;
    private String methodBody;

    public MethodBuilder() {
        name = null;
        kind = null;
        comment = "";
        annotations = new ArrayList<Annotation>();
        modifiers = EnumSet.noneOf(MethodModifier.class);
        exceptions = new ArrayList<QualifiedName>();
        typeParameters = new ArrayList<TypeParameter>();
        returnType = new TypeSymbol(QualifiedName.valueOf("void"));
        parameters = new ArrayList<Parameter>();
        methodBody = "";
    }

    public MethodBuilder(final Method prototype) {
        this.name = prototype.getName();
        this.kind = prototype.getKind();
        this.comment = prototype.getComment();
        this.annotations = new ArrayList<Annotation>(prototype.getAnnotations());
        this.modifiers = prototype.getModifiers().isEmpty() ? EnumSet.noneOf(MethodModifier.class) : EnumSet.copyOf(prototype.getModifiers());
        this.exceptions = new ArrayList<QualifiedName>(prototype.getExceptions());
        this.typeParameters = new ArrayList<TypeParameter>(prototype.getTypeParameters());
        this.returnType = prototype.getReturnType();
        this.parameters = new ArrayList<Parameter>(prototype.getParameters());
        this.methodBody = prototype.getMethodBody();
    }

    public MethodBuilder setName(final SimpleName name) {
        this.name = name;
        return this;
    }

    public MethodBuilder setKind(final Method.Kind kind) {
        this.kind = kind;
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

    public MethodBuilder setTypeParameters(final List<TypeParameter> typeParameters) {
        this.typeParameters = new ArrayList<TypeParameter>(typeParameters);
        return this;
    }
    
    public MethodBuilder addTypeParameter(final TypeParameter typeParameter) {
        this.typeParameters.add(typeParameter);
        return this;
    }

    public MethodBuilder setReturnType(final TypeSymbol returnType) {
        this.returnType = returnType;
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
        return new Method(name, kind, annotations, modifiers, 
                exceptions, typeParameters, returnType,
                parameters, comment, methodBody);
    }
}