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

    public enum Kind { CLASS, INTERFACE, ENUM, ANNOTATION;
    }
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
    private final TypeSymbol superClass;
    /** A list containing the qualified names of all implemented interfaces. */
    @NotNull
    @Valid
    private final List<TypeSymbol> implementedInterfaces;
    /** The TypeParameters. */
    @NotNull
    @Valid
    private final List<TypeParameter> typeParameters;
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
    private final List<Field> fields;
    @NotNull
    @Valid
    private final Set<Import> additionalImports;

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
     * @param superClass The qualified name of the super class.
     * @param implementedInterfaces A list containing the qualified names of all implemented interfaces.
     * @param typeParameters A list containing the TypeParameters.
     * @param methods A list containing all methods of this type.
     * @param fields A list containing all fields of this type.
     */
    public Type(final QualifiedName name,
                final Kind kind,
                final List<Annotation> annotations,
                final Set<TypeModifier> modifiers,
                final String comment,
                final TypeSymbol superClass,
                final List<TypeSymbol> implementedInterfaces,
                final List<TypeParameter> typeParameters,
                final List<Method> methods,
                final List<Field> fields,
                final Set<Import> additionalImports) {
        this.implementedInterfaces = Collections.unmodifiableList(new ArrayList<TypeSymbol>(implementedInterfaces));
        this.kind = kind;
        this.methods = Collections.unmodifiableList(new ArrayList<Method>(methods));
        this.annotations = Collections.unmodifiableList(new ArrayList<Annotation>(annotations));
        this.name = name;
        this.modifiers = modifiers.isEmpty() ? EnumSet.noneOf(TypeModifier.class) : EnumSet.copyOf(modifiers);
        this.superClass = superClass;
        this.typeParameters = Collections.unmodifiableList(new ArrayList<TypeParameter>(typeParameters));
        this.fields = Collections.unmodifiableList(new ArrayList<Field>(fields));
        this.comment = comment;
        this.additionalImports = Collections.unmodifiableSet(new HashSet<Import>(additionalImports));
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
    public List<TypeSymbol> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    /**
     *
     * @return A list containing the TypeParameters.
     */
    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
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
    public TypeSymbol getSuperClass() {
        return superClass;
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
        return superClass.getQualifiedName().toString().equals(Object.class.getSimpleName());
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
     * @return list of imports of this Type.
     */
    public List<QualifiedName> getImports() {
        final Set<Import> allImports = new HashSet<Import>();
        
        // Die Oberklasse:
        if (getKind() != Kind.INTERFACE) {
            final QualifiedName superClass = getSuperClass().getQualifiedName();
            if (superClass != null)
                allImports.add(new Import(superClass));
            // Die bounded classes der super class
            for (final TypeParameter typeParameter : getSuperClass().getTypeParameters()) {
                for (QualifiedName boundedType : typeParameter.getBoundedTypes()) {
                    allImports.add(new Import(boundedType));
                }
            }
        }
        // Alle implementierten Interfaces und deren bounded types:
        for (final TypeSymbol nameOfInterface : implementedInterfaces) {
            allImports.add(new Import(nameOfInterface.getQualifiedName()));
            for (TypeParameter typeParameter : nameOfInterface.getTypeParameters()) {
                for (QualifiedName boundedType : typeParameter.getBoundedTypes()) {
                    allImports.add(new Import(boundedType));
                }
            }
        }

        // Alle Annotationen:
        for (final Annotation annotation : annotations) {
            allImports.add(new Import(annotation.getName()));
        }

        // In Methoden verwendete Typen:
        for (final Method method : methods) {
            // Alle Return-Types sowie die bounded types der TypeParameter:
            if (!method.isConstructor()) {
                addTypeSymbolImports(allImports, method.getReturnType());
            }
            // Die Annotationen aller Methoden:
            for (final Annotation annotation : method.getAnnotations()) {
                allImports.add(new Import(annotation.getName()));
            }
            // Alle Exceptions der Methoden
            for (final QualifiedName exception : method.getExceptions()) {
                allImports.add(new Import(exception));
            }
            // Alle generischen Typen (bounded types):
            for (final TypeParameter typeParam : method.getTypeParameters()) {
                for (final QualifiedName boundedType : typeParam.getBoundedTypes()) {
                    allImports.add(new Import(boundedType));
                }
            }
            // Alle Typen von allen Parametern der Methoden:
            for (final Parameter param : method.getParameters()) {
                if (!param.getType().getQualifiedName().isPrimitive()) {
                    addTypeSymbolImports(allImports, param.getType());
                }
                for (final Annotation annotation : param.getAnnotations()) {
                    allImports.add(new Import(annotation.getName()));
                }
            }
        }
        // In Feldern verwendete Typen:
        for (final Field field : getFields()) {
            // Die Typen aller Attribute:
            addTypeSymbolImports(allImports, field.getType());

            // Die Annotationen aller Attribute
            for (final Annotation annotation : field.getAnnotations()) {
                allImports.add(new Import(annotation.getName()));
            }
        }
        // Alle zusätzlichen, von den method bodies benötigten Imports:
        allImports.addAll(additionalImports);
        // Ergebnisse filtern und sortieren:
        final List<QualifiedName> result = new ArrayList<QualifiedName>(allImports.size());
        for (final Import i : allImports) {
            final QualifiedName qn = i.getQualifiedName();
            if (!qn.isTypeVariable()
                    && !qn.isWildcard()
                    && !QualifiedName.valueOf(qn).getPackage().equals(name.getPackage())
                    && !qn.toString().startsWith("java.lang")) {
                result.add(qn);
            }
        }
        Collections.sort(result);
        return result;
    }

    private static void addTypeSymbolImports(final Set<Import> allImports, final TypeSymbol typeSymbol) {
        final QualifiedName qn = typeSymbol.getQualifiedName();
        if (!qn.isPrimitive()) {
            // we are not interested in wildcards ('?') and type variables ('T') because we can not import them.
            if (!qn.isWildcard() && !qn.isTypeVariable())
                allImports.add(new Import(qn));
            for (final TypeParameter typeParameter : typeSymbol.getTypeParameters()) {
                // again, we are not interested in wildcards ('?') and type variables ('T') because we can not
                // import them.
                if (!qn.isWildcard() && !qn.isTypeVariable())
                    allImports.add(new Import(typeParameter.getParamName()));
                for (final QualifiedName boundedType : typeParameter.getBoundedTypes()) {
                    // bounded type should never be wildcards or type variables:
                    if (boundedType.isWildcard() || boundedType.isTypeVariable())
                        throw new IllegalStateException("Something strange happened: a bounded type is a wildcard or type-variable...");
                    allImports.add(new Import(boundedType));
                }
            }
        }
    }

    public Collection<Import> getAdditionalImports() {
        return additionalImports;
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
        if (!implementedInterfaces.equals(type.implementedInterfaces)) return false;
        if (!superClass.equals(type.superClass)) return false;
        if (!additionalImports.equals(type.additionalImports)) return false;

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + kind.hashCode();
        result = 31 * result + (comment.hashCode());
        result = 31 * result + annotations.hashCode();
        result = 31 * result + superClass.hashCode();
        result = 31 * result + implementedInterfaces.hashCode();
        result = 31 * result + methods.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + fields.hashCode();
        result = 31 * result + additionalImports.hashCode();
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
        final StringBuilder sb = new StringBuilder();
        if (modifiers.contains(TypeModifier.PUBLIC))
            sb.append("public ");
        if (modifiers.contains(TypeModifier.STATIC))
            sb.append("static ");
        if (modifiers.contains(TypeModifier.ABSTRACT))
            sb.append("abstract ");
        if (modifiers.contains(TypeModifier.FINAL))
            sb.append("final ");
        switch (kind) {
            case CLASS:
                sb.append("class ");
                break;
            case ENUM:
                sb.append("enum ");
                break;
            case INTERFACE:
                sb.append("interface ");
                break;
            case ANNOTATION:
                throw new IllegalStateException("Annotation types are not yet implemented.");
            default:
                throw new IllegalStateException(kind + " is not yet implemented.");
        }
        sb.append(name.getSimpleName());
        if (!typeParameters.isEmpty()) {
            sb.append("<");
            boolean firstParam = true;
            for (final TypeParameter param : typeParameters) {
                if (firstParam)
                    firstParam = false;
                else
                    sb.append(", ");
                sb.append(param.toString());
            }
            sb.append(">");
        }
        return sb.toString();
    }

}
