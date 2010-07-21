/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import java.util.*;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeBuilder {
    private QualifiedName name;
    private Type.Kind kind = Type.Kind.CLASS;
    private List<Annotation> annotations = new ArrayList<Annotation>();
    private EnumSet<TypeModifier> modifiers = EnumSet.noneOf(TypeModifier.class);
    private String comment = "";
    private TypeSymbol superClass = new TypeSymbol(QualifiedName.valueOf("java.lang.Object"));
    private List<TypeSymbol> implementedInterfaces = new ArrayList<TypeSymbol>();
    private List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
    private List<Method> methods = new ArrayList<Method>();
    private List<Field> fields = new ArrayList<Field>();
    private Set<QualifiedName> addedImports = new HashSet<QualifiedName>();

    public TypeBuilder() {
    }

    public TypeBuilder(final Type prototype) {
        name = prototype.getName();
        annotations.addAll(prototype.getAnnotations());
        modifiers.addAll(prototype.getModifiers());
        comment = prototype.getComment();
        superClass = prototype.getSuperClass();
        implementedInterfaces.addAll(prototype.getImplementedInterfaces());
        methods.addAll(prototype.getMethods());
        fields.addAll(prototype.getFields());
        kind = prototype.getKind();
        addedImports.addAll(prototype.getAdditionalImports());
    }

    public TypeBuilder setName(final QualifiedName name) {
        this.name = name;
        return this;
    }

    public TypeBuilder setKind(final Type.Kind kind) {
        this.kind = kind;
        return this;
    }
    
    public TypeBuilder setAnnotations(final List<Annotation> annotations) {
        this.annotations = new ArrayList<Annotation>(annotations);
        return this;
    }

    public TypeBuilder addAnnotation(final Annotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public TypeBuilder setImplementedInterfaces(final List<TypeSymbol> implementedInterfaces) {
        this.implementedInterfaces = new ArrayList<TypeSymbol>(implementedInterfaces);
        return this;
    }

    public TypeBuilder addImplementedInterface(final TypeSymbol implementedInterface) {
        this.implementedInterfaces.add(implementedInterface);
        return this;
    }

    public TypeBuilder setTypeParameters(final List<TypeParameter> typeParameters) {
        this.typeParameters = new ArrayList<TypeParameter>(typeParameters);
        return this;
    }

    public TypeBuilder addTypeParameter(final TypeParameter typeParameter) {
        this.typeParameters.add(typeParameter);
        return this;
    }

    public TypeBuilder setMethods(final List<Method> methods) {
        this.methods = new ArrayList<Method>(methods);
        return this;
    }

    public TypeBuilder addMethod(final Method method) {
        methods.add(method);
        return this;
    }

    public TypeBuilder addModifier(final TypeModifier modifier) {
        modifiers.add(modifier);
        return this;
    }

    public TypeBuilder setModifiers(final Set<TypeModifier> modifiers) {
        this.modifiers = EnumSet.copyOf(modifiers);
        return this;
    }

    public TypeBuilder setComment(final String comment) {
        this.comment = comment;
        return this;
    }

    public TypeBuilder setSuperClass(final TypeSymbol superClass) {
        this.superClass = superClass;
        return this;
    }

    public TypeBuilder setFields(final List<Field> fields) {
        this.fields = new ArrayList<Field>(fields);
        return this;
    }

    public TypeBuilder addField(final Field field) {
        fields.add(field);
        return this;
    }

    public void addImport(QualifiedName qualifiedName) {
        addedImports.add(qualifiedName);
    }

    public void addImports(Set<QualifiedName> qualifiedNames) {
        addedImports.addAll(qualifiedNames);
    }

    public Type toType() {
        return new Type(name, kind, annotations, modifiers, comment,
                superClass, implementedInterfaces, typeParameters, methods,
                fields, addedImports);
    }
}
