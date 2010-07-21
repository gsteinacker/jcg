/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.translation;

import de.steinacker.jcg.model.*;
import de.steinacker.jcg.transform.type.TypeMessage;
import de.steinacker.jcg.transform.type.TypeTransformer;
import de.steinacker.jcg.util.NameUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Translates types from one language into another language, using a Glossary.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeTranslator implements TypeTransformer {

    /** A list of possible prefixes of method names, where the remaining part of the
     * method name may be translatable.
     * TODO: make list configurable!
     */
    private static final List<String> translatableNamePrefixes = Arrays.asList(
            "is", "has", "set", "get", "add", "remove", "put"
    );

    private final Glossary glossary;

    public TypeTranslator(final Glossary glossary) {
        this.glossary = glossary;
    }

    @Override
    public String getName() {
        return "TypeTranslator";
    }

    @Override
    public List<TypeMessage> transform(TypeMessage message) {
        final Type type = message.getPayload();
        final TypeBuilder typeBuilder = new TypeBuilder(type);
        // Translate the name:
        typeBuilder.setName(translateQualifiedName(type.getName()));

        // Translate the fields:
        typeBuilder.setFields(new ArrayList<Field>());
        for (final Field field : type.getFields()) {
            typeBuilder.addField(transform(field));
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

        // Translate methods
        typeBuilder.setMethods(new ArrayList<Method>());
        for (final Method method : type.getMethods()) {
            typeBuilder.addMethod(transform(method));
        }

        return Collections.singletonList(new TypeMessage(typeBuilder.toType(), message.getContext()));
    }

    private Method transform(final Method method) {
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

    private Field transform(final Field field) {
        return new FieldBuilder()
                .setName(translateSimpleName(field.getName()))
                .setType(translateTypeSymbol(field.getType()))
                .setInitString(field.getInitString())
                .setAnnotations(field.getAnnotations())
                .setModifiers(field.getModifiers())
                .setComment(field.getComment())
                .toField();
    }

    private SimpleName translateSimpleName(final CharSequence sourceName) {
        if (glossary.hasTranslation(sourceName, glossary.getSourceLanguage())) {
            final String translation = glossary.getTranslation(sourceName, glossary.getSourceLanguage());
            final boolean firstUpperCase = NameUtil.isFirstUpperCase(sourceName);
            return SimpleName.valueOf(NameUtil.toCamelHumpName(translation, firstUpperCase));
        } else {
            return SimpleName.valueOf(sourceName);
        }
    }

    private QualifiedName translateQualifiedName(final QualifiedName sourceName) {
        final SimpleName simpleName = translateSimpleName(sourceName.getSimpleName());
        return QualifiedName.valueOf(sourceName.getPackage(), simpleName);
    }

    private SimpleName translateCamelHumpName(final CharSequence name) {
        String[] parts = null;
        final String s = name.toString();
        String methodPrefix = "";
        for (final String prefix : translatableNamePrefixes) {
            if (s.startsWith(prefix) && prefix.length() < s.length()) {
                methodPrefix = prefix;
                parts = NameUtil.splitCamelHumpName(s.substring(prefix.length()));
            }
        }
        if (parts != null) {
            final StringBuilder camelHumpName = new StringBuilder();
            for (String part : parts) {
                camelHumpName.append(translateSimpleName(part));
            }
            return SimpleName.valueOf(methodPrefix + NameUtil.toCamelHumpName(camelHumpName.toString(), true));
        } else {
            return translateSimpleName(name);
        }
    }

    private TypeSymbol translateTypeSymbol(final TypeSymbol typeSymbol) {
        final QualifiedName translatedClassName = translateQualifiedName(typeSymbol.getQualifiedName());
        final List<TypeParameter> translatedTypeParams = new ArrayList<TypeParameter>();
        for (final TypeParameter typeParameter : typeSymbol.getTypeParameters()) {
            final List<QualifiedName> translatedBoundedTypes = new ArrayList<QualifiedName>();
            for (final QualifiedName boundedType : typeParameter.getBoundedTypes()) {
                translatedBoundedTypes.add(translateQualifiedName(boundedType));
            }
            final QualifiedName translatedParamName = translateQualifiedName(typeParameter.getParamName());
            translatedTypeParams.add(new TypeParameter(translatedParamName, translatedBoundedTypes));
        }
        return new TypeSymbol(translatedClassName, translatedTypeParams);
    }

    @Override
    public String toString() {
        return getName();
    }

}
