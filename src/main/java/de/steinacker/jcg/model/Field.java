/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Stores details of fields in the java code
 *
 * @author Guido Steinacker
 */
public final class Field implements Annotatable {
    @NotNull
    @Valid
    private final SimpleName name;
    @NotNull
    @Valid
    private final TypeSymbol type;
    @NotNull
    private final String initString;
    @NotNull
    @Valid
    private final List<Annotation> annotations;
    @NotNull
    private final Set<FieldModifier> modifiers;
    @NotNull
    private final String comment;

    public Field(final SimpleName name,
                 final TypeSymbol type,
                 final String initString,
                 final List<Annotation> annotations,
                 final Set<FieldModifier> modifiers,
                 final String comment) {
        this.name = name;
        this.type = type;
        this.initString = initString != null ? initString : "";
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.comment = comment != null ? comment : "";
    }

    public SimpleName getName() {
        return name;
    }

    public TypeSymbol getType() {
        return type;
    }

    public String getInitString() {
        return initString;
    }

    public List<Annotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public Set<FieldModifier> getModifiers() {
        return Collections.unmodifiableSet(modifiers);
    }

    public boolean is(final FieldModifier modifier) {
        return modifiers.contains(modifier);
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Field field = (Field) o;

        if (!annotations.equals(field.annotations)) return false;
        if (!comment.equals(field.comment)) return false;
        if (!initString.equals(field.initString)) return false;
        if (!modifiers.equals(field.modifiers)) return false;
        if (!name.equals(field.name)) return false;
        if (!type.equals(field.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + initString.hashCode();
        result = 31 * result + annotations.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + comment.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns the signature of a Field in a format, that is usable in source code.
     * The resulting String does not contain comments or annotations.
     *
     * @return String representation of the field's signature
     */
    @Override
    public String toString() {
        final StringBuilder sigBuilder = new StringBuilder();
        final Set<FieldModifier> modifiers = getModifiers();
        if (modifiers.contains(FieldModifier.PUBLIC))
            sigBuilder.append("public ");
        if (modifiers.contains(FieldModifier.PROTECTED))
            sigBuilder.append("protected ");
        if (modifiers.contains(FieldModifier.PRIVATE))
            sigBuilder.append("private ");
        if (modifiers.contains(FieldModifier.STATIC))
            sigBuilder.append("static ");
        if (modifiers.contains(FieldModifier.FINAL))
            sigBuilder.append("final ");
        if (modifiers.contains(FieldModifier.TRANSIENT))
            sigBuilder.append("transient ");
        if (modifiers.contains(FieldModifier.VOLATILE))
            sigBuilder.append("volatile ");

        sigBuilder.append(getType().toString())
                .append(" ")
                .append(getName());
        if (!initString.isEmpty()) {
            sigBuilder.append(" = ").append(initString);
        }
        return sigBuilder.toString();
    }
}