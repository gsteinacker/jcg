/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static de.steinacker.jcg.model.MethodModifier.*;

/**
 * Stores method information of java class
 *
 * @author Guido Steinacker
 */
public final class Method implements Annotatable {
    @NotNull
    @Valid
    private final SimpleName name;
    @NotNull
    @Valid
    private final List<Annotation> annotations;
    @NotNull
    private final Set<MethodModifier> modifiers;
    @NotNull
    @Valid
    private final List<QualifiedName> exceptions;
    @Null
    @Valid
    private final QualifiedName returnTypeName;
    @NotNull
    @Valid
    private final List<Parameter> parameters;
    @NotNull
    private final String comment;
    @NotNull
    private final String methodBody;


    public Method(final SimpleName name,
                  final List<Annotation> annotations,
                  final Set<MethodModifier> modifiers,
                  final List<QualifiedName> exceptions,
                  final QualifiedName returnTypeName,
                  final List<Parameter> parameters,
                  final String comment,
                  final String methodBody) {
        this.name = name;
        this.annotations = Collections.unmodifiableList(new ArrayList<Annotation>(annotations));
        this.modifiers = modifiers;
        this.exceptions = exceptions;
        this.returnTypeName = returnTypeName;
        this.parameters = parameters;
        this.comment = comment;
        this.methodBody = methodBody;
    }

    public SimpleName getName() {
        return name;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public Set<MethodModifier> getModifiers() {
        return Collections.unmodifiableSet(modifiers);
    }

    public List<QualifiedName> getExceptions() {
        return Collections.unmodifiableList(exceptions);
    }

    public QualifiedName getReturnTypeName() {
        return returnTypeName;
    }

    public List<Parameter> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public boolean isConstructor() {
        return returnTypeName == null;
    }

    public boolean is(final MethodModifier modifier) {
        return modifiers.contains(modifier);
    }

    public String getComment() {
        return comment;
    }

    public String getMethodBody() {
        return methodBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Method method = (Method) o;

        if (!annotations.equals(method.annotations)) return false;
        if (!comment.equals(method.comment)) return false;
        if (!exceptions.equals(method.exceptions)) return false;
        if (!methodBody.equals(method.methodBody)) return false;
        if (!modifiers.equals(method.modifiers)) return false;
        if (!name.equals(method.name)) return false;
        if (!parameters.equals(method.parameters)) return false;
        if (returnTypeName != null ? !returnTypeName.equals(method.returnTypeName) : method.returnTypeName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + annotations.hashCode();
        result = 31 * result + modifiers.hashCode();
        result = 31 * result + exceptions.hashCode();
        result = 31 * result + (returnTypeName != null ? returnTypeName.hashCode() : 0);
        result = 31 * result + parameters.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + methodBody.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns the Signature of a Method in a format, that is usable in source code.
     * The resulting String does not contain comments or annotations.
     *
     * @return String representation of the method's signature
     */
    @Override
    public String toString() {
        final StringBuilder sigBuilder = new StringBuilder();
        // TODO: wenn modifiers "korrekt" sortiert sind, geht das hier deutlich einfacher...
        if (modifiers.contains(PUBLIC))
            sigBuilder.append("public ");
        if (modifiers.contains(PROTECTED))
            sigBuilder.append("protected ");
        if (modifiers.contains(PRIVATE))
            sigBuilder.append("private ");
        if (modifiers.contains(STATIC))
            sigBuilder.append("static ");
        if (modifiers.contains(ABSTRACT))
            sigBuilder.append("abstract ");
        if (modifiers.contains(FINAL))
            sigBuilder.append("final ");
        if (modifiers.contains(SYNCHRONIZED))
            sigBuilder.append("synchronized ");
        if (!isConstructor()) {
            sigBuilder.append(returnTypeName.getSimpleName()).append(" ");
        }
        sigBuilder.append(name.toString())
                .append("(");
        boolean isFirst = true;
        for (final Parameter parameter : parameters) {
            if (!isFirst) {
                sigBuilder.append(", ");
            } else {
                isFirst = false;
            }
            sigBuilder.append(parameter.isFinal() ? "final " : "")
                    .append(parameter.getTypeName().getSimpleName().toString())
                    .append(" ")
                    .append(parameter.getName().toString());
        }
        sigBuilder.append(")");
        return sigBuilder.toString();
    }

}