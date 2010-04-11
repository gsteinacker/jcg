/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * A class, interface, enum or annotation type.
 * 
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class Type implements Annotatable {

    public enum Kind { CLASS, INTERFACE, ENUM, ANNOTATION }

    /** The qualified Name of the type (e.g. "java.lang.String"). */
    @NotNull
    @Valid
    private final QualifiedName name;
    /** The kind of Type: class, interface, enum or annotation (@interface). */
    @NotNull
    private final Kind kind;
    /** An optional comment of this class. Null means "no comments". */
    @NotNull
    private final String comment;
    /** A list of type annotations. */
    @NotNull
    @Valid
    private final List<Annotation> annotations;
    /** The qualified name of the super class. */
    @NotNull
    @Valid
    private final QualifiedName nameOfSuperClass;
    /** A list containing the qualified names of all implemented interfaces. */
    @NotNull
    @Valid
    private final List<QualifiedName> nameOfInterfaces;
    /** A list containing all methods of this type. */
    @NotNull
    @Valid
    private final List<Method> methods;
    /** A set of Type Modifiers. This set must not contain combinations
     * that are not valid in Java like, for example, ABSTRACT and FINAL.
     */
    @NotNull
    @Valid
    private final Set<TypeModifier> modifiers;
    /** A list containing all fields of this type. */
    @NotNull
    @Valid
    private List<Field> fields;

    /**
     * Creates a new Type instance.
     * Because this constructor has a rather long parameter list, you might prefer using
     * the {@link TypeBuilder} to create Type instances.
     *
     * @param name The qualified Name of the type (e.g. "java.lang.String")
     * @param kind The kind of Type: class, interface, enum or annotation (@interface)
     * @param annotations A list of type annotations.
     * @param modifiers A set of Type Modifiers. This set must not contain combinations
     * which are not valid in Java like, for example, ABSTRACT and FINAL.
     * @param comment Comments of this class.
     * @param nameOfSuperClass The qualified name of the super class.
     * @param nameOfInterfaces A list containing the qualified names of all implemented interfaces.
     * @param methods A list containing all methods of this type.
     * @param fields A list containing all fields of this type.
     */
    public Type(final QualifiedName name,
                final Kind kind,
                final List<Annotation> annotations,
                final Set<TypeModifier> modifiers,
                final String comment,
                final QualifiedName nameOfSuperClass,
                final List<QualifiedName> nameOfInterfaces,
                final List<Method> methods,
                final List<Field> fields) {
        this.nameOfInterfaces = new ArrayList<QualifiedName>(nameOfInterfaces);
        this.kind = kind;
        this.methods = new ArrayList<Method>(methods);
        this.annotations = new ArrayList<Annotation>(annotations);
        this.name = name;
        this.modifiers = modifiers.isEmpty() ? EnumSet.noneOf(TypeModifier.class) : EnumSet.copyOf(modifiers);
        this.nameOfSuperClass = nameOfSuperClass;
        this.fields = new ArrayList<Field>(fields);
        this.comment = comment;
        //Collections.sort(this.fields, new FieldComparator());
        //Collections.sort(this.methods, new MethodComparator());
        //Collections.sort(this.annotations, new AnnotationComparator());
        //Collections.sort(this.modifiers, new ModifierComparator());
        //Collections.sort(this.nameOfInterfaces);
    }

    /**
     *
     * @return The qualified Name of the type (e.g. "java.lang.String").
     */
    public QualifiedName getName() {
        return name;
    }

    /**
     *
     * @return The kind of Type: class, interface, enum or annotation (@interface).
     */
    public Kind getKind() {
        return kind;
    }

    /**
     *
     * @return A list of type annotations.
     */
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     *
     * @return A list containing the qualified names of all implemented interfaces.
     */
    public List<QualifiedName> getNameOfInterfaces() {
        return nameOfInterfaces;
    }

    /**
     * @return all the methods that are present in this class.
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     *
     * @return A set of Type Modifiers. This set must not contain combinations
     * which are not valid in Java like, for example, ABSTRACT and FINAL.
     */
    public Set<TypeModifier> getModifiers() {
        return modifiers;
    }

    /**
     *
     * @return Comments of this class.
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @return The qualified name of the super class.
     */
    public QualifiedName getNameOfSuperClass() {
        return nameOfSuperClass;
    }

    /**
     * @return all the fields that are present in this class.
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Returns the field with the specified name, or null if there is no such Field.
     *
     * @param fieldName the name (SimpleName) of the requested field.
     * @return Field or null
     */
    public Field getField(final SimpleName fieldName) {
        for (final Field field : fields) {
            if (field.getName().equals(fieldName))
                return field;
        }
        return null;
    }

    /**
     * Returns true if this class is a top-level class.
     * Top-level classes are classes which only extend java.lang.Object.
     *
     * @return true, if this class only extends java.lang.Object.
     */
    public boolean isTopLevelClass() {
        return nameOfSuperClass.toString().equals(Object.class.getSimpleName());
    }

    /**
     * Returns true if this type has the specified modifier.
     *
     * @param modifier the modifier
     * @return true if <code>modifier</code> is member of the set returned by <code>getModifiers()</code>.
     */
    public boolean is(final TypeModifier modifier) {
        return modifiers.contains(modifier);
    }

    /**
     * Returns a list containing all imports of this type.
     *
     * @return
     */
    public List<QualifiedName> getImports() {
        final Set<QualifiedName> allImports = new HashSet<QualifiedName>();
        
        // Die Oberklasse:
        final QualifiedName superClass = getNameOfSuperClass();
        if (superClass != null)
            allImports.add(superClass);

        // Alle implementierten Interfaces:
        for (final QualifiedName nameOfInterface : nameOfInterfaces) {
            allImports.add(nameOfInterface);
        }
        // Alle Annotationen:
        for (final Annotation annotation : annotations) {
            allImports.add(annotation.getName());
        }

        // In Methoden verwendete Typen:
        for (final Method method : methods) {
            // Alle Return-Types
            if (!method.isConstructor()) {
                final QualifiedName returnTypeName = method.getReturnTypeName();
                if (!returnTypeName.isPrimitive())
                    allImports.add(returnTypeName);
            }
            // Die Annotationen aller Methoden:
            for (final Annotation annotation : method.getAnnotations()) {
                allImports.add(annotation.getName());
            }
            // Alle Exceptions der Methoden
            for (final QualifiedName exception : method.getExceptions()) {
                allImports.add(exception);
            }
            // Alle Parameter-Typen der Methoden:
            for (final Parameter param : method.getParameters()) {
                if (!param.getTypeName().isPrimitive()) {
                    allImports.add(param.getTypeName());
                }
                for (final Annotation annotation : param.getAnnotations()) {
                    allImports.add(annotation.getName());
                }
            }
        }
        // In Feldern verwendete Typen:
        for (final Field field : getFields()) {
            // Die Typen aller Attribute:
            if (!field.getTypeName().isPrimitive())
                allImports.add(field.getTypeName());
            // Die Annotationen aller Attribute
            for (final Annotation annotation : field.getAnnotations()) {
                allImports.add(annotation.getName());
            }
        }
        // Ergebnisse filtern und sortieren:
        final List<QualifiedName> result = new ArrayList<QualifiedName>(allImports.size());
        for (final QualifiedName i : allImports) {
            if (!QualifiedName.valueOf(i).getPackage().equals(name.getPackage()) && !i.toString().startsWith("java.lang")) {
                result.add(i);
            }
        }
        Collections.sort(result);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Type type = (Type) o;

        if (!annotations.equals(type.annotations)) return false;
        if (!comment.equals(type.comment)) return false;
        if (!fields.equals(type.fields)) return false;
        if (kind != type.kind) return false;
        if (!methods.equals(type.methods)) return false;
        if (!modifiers.equals(type.modifiers)) return false;
        if (!name.equals(type.name)) return false;
        if (!nameOfInterfaces.equals(type.nameOfInterfaces)) return false;
        if (!nameOfSuperClass.equals(type.nameOfSuperClass)) return false;

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + kind.hashCode();
        result = 31 * result + (comment.hashCode());
        result = 31 * result + annotations.hashCode();
        result = 31 * result + nameOfSuperClass.hashCode();
        result = 31 * result + nameOfInterfaces.hashCode();
        result = 31 * result + methods.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + fields.hashCode();
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
        switch (kind) {
            case CLASS:
                sigBuilder.append("class ");
                break;
            case ENUM:
                sigBuilder.append("enum ");
                break;
            case INTERFACE:
                sigBuilder.append("interface ");
                break;
            case ANNOTATION:
                throw new IllegalStateException("Annotation types are not yet implemented.");
            default:
                throw new IllegalStateException(kind + " is not yet implemented.");
        }
        sigBuilder.append(name.getSimpleName());
        return sigBuilder.toString();
    }

}
