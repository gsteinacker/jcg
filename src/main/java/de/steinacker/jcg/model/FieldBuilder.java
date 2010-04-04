/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import java.util.*;

public final class FieldBuilder {
    private SimpleName name;
    private QualifiedName typeName;
    private String initString;
    private List<Annotation> annotations;
    private Set<FieldModifier> modifiers;
    private String comment;

    public FieldBuilder() {
        annotations = new ArrayList<Annotation>();
        modifiers = new LinkedHashSet<FieldModifier>();
        comment = "";
        initString = "";
    }

    public FieldBuilder(final Field prototype) {
        name = prototype.getName();
        typeName = prototype.getTypeName();
        initString = prototype.getInitString();
        annotations = new ArrayList<Annotation>(prototype.getAnnotations());
        modifiers = prototype.getModifiers().isEmpty()
                ? EnumSet.noneOf(FieldModifier.class)
                : EnumSet.copyOf(prototype.getModifiers());
        comment = prototype.getComment();
    }

    public FieldBuilder setName(SimpleName name) {
        this.name = name;
        return this;
    }

    public FieldBuilder setTypeName(QualifiedName typeName) {
        this.typeName = typeName;
        return this;
    }

    public FieldBuilder setInitString(final String initString) {
        this.initString = initString;
        return this;
    }

    public FieldBuilder setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    public FieldBuilder setModifiers(Set<FieldModifier> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public FieldBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Field toField() {
        return new Field(name, typeName, initString, annotations, modifiers, comment);
    }
}