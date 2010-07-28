/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.*;

import java.util.*;

/**
 * @author Guido Steinacker
 * @since 28.07.2010
 */
public abstract class AbstractTypeTranslator implements TypeTransformer {

    @Override
    public List<TypeMessage> transform(TypeMessage message) {
        final Type type = message.getPayload();
        final TypeBuilder typeBuilder = new TypeBuilder(type);
        // Translate the name:
        typeBuilder.setName(translateQualifiedName(type.getName()));

        // Translate the type parameters:
        typeBuilder.setTypeParameters(translateTypeParameters(type.getTypeParameters()));

        // Translate the additional imports:
        typeBuilder.setImports(translateImports(type.getImports()));

        // Translate the fields:
        typeBuilder.setFields(new ArrayList<Field>());
        for (final Field field : type.getFields()) {
            typeBuilder.addField(translateField(field));
        }

        // Translate methods
        typeBuilder.setMethods(new ArrayList<Method>());
        for (final Method method : type.getMethods()) {
            typeBuilder.addMethod(translateMethod(method));
        }

        // Translate the parent type:
        final TypeSymbol superClass = type.getSuperClass();
        if (superClass != null) {
            final TypeSymbol translatedSuperClass = translateTypeSymbol(superClass);
            typeBuilder.setSuperClass(translatedSuperClass);
        }

        // Translate the interfaces:
        typeBuilder.setImplementedInterfaces(new ArrayList<TypeSymbol>());
        final List<TypeSymbol> implementedInterfaces = type.getImplementedInterfaces();
        for (final TypeSymbol implementedInterface : implementedInterfaces) {
            typeBuilder.addImplementedInterface(translateTypeSymbol(implementedInterface));
        }

        return Collections.singletonList(new TypeMessage(typeBuilder.toType(), message.getContext()));
    }

    private Set<Import> translateImports(SortedSet<Import> imports) {
        final Set<Import> result = new HashSet<Import>();
        for (final Import anImport : imports) {
            result.add(new Import(translateQualifiedName(anImport.getQualifiedName()), anImport.isStatic()));
        }
        return result;
    }

    private Method translateMethod(final Method method) {
        final MethodBuilder mb = new MethodBuilder(method);
        final SimpleName methodName = method.getName();
        if (method.isConstructor()) {
            mb.setReturnType(null);
            mb.setName(translateSimpleName(methodName));
        } else {
            final TypeSymbol returnType = method.getReturnType();
            final QualifiedName translatedType = translateQualifiedName(returnType.getQualifiedName());
            mb.setReturnType(new TypeSymbol(translatedType, returnType.getTypeParameters()));
            mb.setName(translateCamelHumpName(methodName));
        }
        mb.setParameters(new ArrayList<Parameter>());
        for (final Parameter parameter : method.getParameters()) {
            mb.addParameter(new ParameterBuilder()
                    .setType(translateTypeSymbol(parameter.getType()))
                    .setName(translateSimpleName(parameter.getName()))
                    .setAnnotations(parameter.getAnnotations())
                    .setFinal(parameter.isFinal())
                    .setComment(parameter.getComment())
                    .toParameter());
        }
        return mb.toMethod();
    }

    private Field translateField(final Field field) {
        return new FieldBuilder()
                .setName(translateSimpleName(field.getName()))
                .setType(translateTypeSymbol(field.getType()))
                .setInitString(field.getInitString())
                .setAnnotations(field.getAnnotations())
                .setModifiers(field.getModifiers())
                .setComment(field.getComment())
                .toField();
    }

    private TypeSymbol translateTypeSymbol(final TypeSymbol typeSymbol) {
        final QualifiedName translatedClassName = translateQualifiedName(typeSymbol.getQualifiedName());
        final List<TypeParameter> translatedTypeParams = translateTypeParameters(typeSymbol.getTypeParameters());
        return new TypeSymbol(translatedClassName, translatedTypeParams);
    }

    private List<TypeParameter> translateTypeParameters(final List<TypeParameter> typeParameters) {
        final List<TypeParameter> translatedTypeParams = new ArrayList<TypeParameter>();
        for (final TypeParameter typeParameter : typeParameters) {
            final List<QualifiedName> translatedBoundedTypes = new ArrayList<QualifiedName>();
            for (final QualifiedName boundedType : typeParameter.getBoundedTypes()) {
                translatedBoundedTypes.add(translateQualifiedName(boundedType));
            }
            final QualifiedName translatedParamName = translateQualifiedName(typeParameter.getParamName());
            translatedTypeParams.add(new TypeParameter(translatedParamName, translatedBoundedTypes));
        }
        return translatedTypeParams;
    }

    protected abstract SimpleName translateSimpleName(CharSequence sourceName);

    protected abstract QualifiedName translateQualifiedName(QualifiedName sourceName);

    protected abstract SimpleName translateCamelHumpName(CharSequence name);
}
