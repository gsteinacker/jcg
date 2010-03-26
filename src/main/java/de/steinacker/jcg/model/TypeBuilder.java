/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeBuilder {
    private QualifiedName name;
    private List<Annotation> annotations = new ArrayList<Annotation>();
    private EnumSet<TypeModifier> modifiers = EnumSet.noneOf(TypeModifier.class);
    private String comment = "";
    private QualifiedName nameOfSuperClass = QualifiedName.valueOf("java.lang.Object");
    private List<QualifiedName> nameOfInterfaces = new ArrayList<QualifiedName>();
    private List<Method> methods = new ArrayList<Method>();
    private List<Field> fields = new ArrayList<Field>();
    private boolean isArray = false;
    private boolean isInterface = false;

    public TypeBuilder() {
    }

    public TypeBuilder(final Type prototype) {
        name = prototype.getName();
        annotations.addAll(prototype.getAnnotations());
        modifiers.addAll(prototype.getModifiers());
        comment = prototype.getComment();
        nameOfSuperClass = prototype.getNameOfSuperClass();
        nameOfInterfaces.addAll(prototype.getNameOfInterfaces());
        methods.addAll(prototype.getMethods());
        fields.addAll(prototype.getFields());
        isArray = prototype.isArray();
        isInterface = prototype.isInterface();
    }

    public TypeBuilder setName(final QualifiedName name) {
        this.name = name;
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

    public TypeBuilder setNameOfInterfaces(final List<QualifiedName> nameOfInterfaces) {
        this.nameOfInterfaces = new ArrayList<QualifiedName>(nameOfInterfaces);
        return this;
    }

    public TypeBuilder addNameOfInterface(final QualifiedName nameOfInterface) {
        this.nameOfInterfaces.add(nameOfInterface);
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

    public TypeBuilder setNameOfSuperClass(final QualifiedName nameOfSuperClass) {
        this.nameOfSuperClass = nameOfSuperClass;
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

    public TypeBuilder setIsArray(final boolean isArray) {
        this.isArray = isArray;
        return this;
    }

    public TypeBuilder setIsInterface(final boolean isInterface) {
        this.isInterface = isInterface;
        return this;
    }

    public Type toType() {
        return new Type(name, annotations, modifiers, comment,
                nameOfSuperClass, nameOfInterfaces, methods,
                fields, isArray, isInterface);
    }
}
