/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class Type implements Annotatable {
    @NotNull
    @Valid
    private final QualifiedName name;
    @NotNull
    private final String comment;
    @NotNull
    @Valid
    private final List<Annotation> annotations;
    @NotNull
    @Valid
    private final List<QualifiedName> nameOfInterfaces;
    @NotNull
    @Valid
    private final List<Method> methods;
    @NotNull
    @Valid
    private Set<TypeModifier> modifiers;
    @NotNull
    @Valid
    private QualifiedName nameOfSuperClass;
    @NotNull
    @Valid
    private List<Field> fields;
    private final boolean isArray;
    private final boolean isInterface;


    public Type(final QualifiedName name,
                final List<Annotation> annotations,
                final Set<TypeModifier> modifiers,
                final String comment,
                final QualifiedName nameOfSuperClass,
                final List<QualifiedName> nameOfInterfaces,
                final List<Method> methods,
                final List<Field> fields,
                final boolean isArray,
                final boolean isInterface) {
        this.nameOfInterfaces = nameOfInterfaces;
        this.methods = methods;
        this.annotations = annotations;
        this.name = name;
        this.modifiers = modifiers;
        this.nameOfSuperClass = nameOfSuperClass;
        this.fields = fields;
        this.isArray = isArray;
        this.isInterface = isInterface;
        this.comment = comment;
    }

    public final QualifiedName getName() {
        return name;
    }

    public final List<Annotation> getAnnotations() {
        return annotations;
    }

    public final List<QualifiedName> getNameOfInterfaces() {
        return nameOfInterfaces;
    }

    /**
     * @return all the methods that are present in this class. This includes
     *         methods that are added by compiler as well, e.g. clinit and init
     *         methods.
     */
    public final List<Method> getMethods() {
        return methods;
    }

    public final Set<TypeModifier> getModifiers() {
        return modifiers;
    }

    public String getComment() {
        return comment;
    }

    public final QualifiedName getNameOfSuperClass() {
        return nameOfSuperClass;
    }

    public final List<Field> getFields() {
        return fields;
    }

    public final Field getField(final SimpleName fieldName) {
        for (Field field : fields) {
            if (field.getName().equals(fieldName))
                return field;
        }
        return null;
    }

    public final boolean isTopLevelClass() {
        return nameOfSuperClass.toString().equals(Object.class.getSimpleName());
    }

    public final boolean isArray() {
        return isArray;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    public boolean isEnum() {
        throw new UnsupportedOperationException("Enum types are not yet implemented.");
    }

    public boolean isAnnotation() {
        throw new UnsupportedOperationException("Annotation types are not yet implemented.");
    }

    public boolean is(final TypeModifier modifier) {
        return modifiers.contains(modifier);
    }


    public final List<String> getImports() {
        final Set<String> allImports = new HashSet<String>();
        // Die Oberklasse:
        allImports.add(getNameOfSuperClass().toString());
        // Alle implementierten Interfaces:
        for (final QualifiedName nameOfInterface : nameOfInterfaces) {
            allImports.add(nameOfInterface.toString());
        }
        // Alle Annotationen:
        for (final Annotation annotation : annotations) {
            allImports.add(annotation.getName().toString());
        }

        // In Methoden verwendete Typen:
        for (final Method method : methods) {
            // Alle Return-Types
            if (!method.isConstructor()) {
                final QualifiedName returnTypeName = method.getReturnTypeName();
                if (!returnTypeName.isPrimitive())
                    allImports.add(returnTypeName.toString());
            }
            // Die Annotationen aller Methoden:
            for (final Annotation annotation : method.getAnnotations()) {
                allImports.add(annotation.getName().toString());
            }
            // Alle Exceptions der Methoden
            for (final QualifiedName exception : method.getExceptions()) {
                allImports.add(exception.toString());
            }
            // Alle Parameter-Typen der Methoden:
            for (final Parameter param : method.getParameters()) {
                if (!param.getTypeName().isPrimitive()) {
                    allImports.add(param.getTypeName().toString());
                }
                for (final Annotation annotation : param.getAnnotations()) {
                    allImports.add(annotation.getName().toString());
                }
            }
        }
        // In Feldern verwendete Typen:
        for (final Field field : getFields()) {
            // Die Typen aller Attribute:
            if (!field.getTypeName().isPrimitive())
                allImports.add(field.getTypeName().toString());
            // Die Annotationen aller Attribute
            for (final Annotation annotation : field.getAnnotations()) {
                allImports.add(annotation.getName().toString());
            }
        }
        // Ergebnisse filtern und sortieren:
        final List<String> result = new ArrayList<String>(allImports.size());
        for (final String i : allImports) {
            if (!i.isEmpty() && !QualifiedName.valueOf(i).getPackage().equals(name.getPackage()) && !i.startsWith("java.lang")) {
                result.add(i);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Type type = (Type) o;

        if (isArray != type.isArray) return false;
        if (isInterface != type.isInterface) return false;
        if (!annotations.equals(type.annotations)) return false;
        if (!comment.equals(type.comment)) return false;
        if (!fields.equals(type.fields)) return false;
        if (!methods.equals(type.methods)) return false;
        if (!modifiers.equals(type.modifiers)) return false;
        if (!name.equals(type.name)) return false;
        if (!nameOfInterfaces.equals(type.nameOfInterfaces)) return false;
        if (!nameOfSuperClass.equals(type.nameOfSuperClass)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + annotations.hashCode();
        result = 31 * result + nameOfInterfaces.hashCode();
        result = 31 * result + methods.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + nameOfSuperClass.hashCode();
        result = 31 * result + fields.hashCode();
        result = 31 * result + (isArray ? 1 : 0);
        result = 31 * result + (isInterface ? 1 : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns the Signature of a Type in a format, that is usable in source code.
     * The resulting String does not contain comments or annotations.
     *
     * @return String representation of the type's signature
     */
    public String toString() {
        final StringBuilder sigBuilder = new StringBuilder();
        if (modifiers.contains(TypeModifier.PUBLIC))
            sigBuilder.append("public ");
        if (modifiers.contains(TypeModifier.STATIC))
            sigBuilder.append("static ");
        if (modifiers.contains(TypeModifier.ABSTRACT))
            sigBuilder.append("abstract ");
        if (modifiers.contains(TypeModifier.FINAL))
            sigBuilder.append("final ");

        if (isInterface())
            sigBuilder.append("interface ");
        else
            sigBuilder.append("class ");
        sigBuilder.append(name.getSimpleName());
        return sigBuilder.toString();
    }

}
