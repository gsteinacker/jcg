/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.translation;

import de.steinacker.jcg.model.*;
import de.steinacker.jcg.transform.type.TypeMessage;
import de.steinacker.jcg.transform.type.TypeTransformer;
import de.steinacker.jcg.util.NameUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Translates types from one language into another language, using a Glossary.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeTranslator implements TypeTransformer {

    private static final Logger LOG = Logger.getLogger(TypeTranslator.class);

    private final Glossary glossary;

    public TypeTranslator(final Glossary glossary) {
        this.glossary = glossary;
    }

    @Override
    public String getName() {
        return "TypeTranslator";
    }

    @Override
    public TypeMessage transform(TypeMessage message) {
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
        typeBuilder.setNameOfSuperClass(translateQualifiedName(type.getNameOfSuperClass()));

        // Translate the interfaces:
        typeBuilder.setNameOfInterfaces(new ArrayList<QualifiedName>());
        final List<QualifiedName> interfacesOfType = type.getNameOfInterfaces();
        for (final QualifiedName interfaceName : interfacesOfType) {
            typeBuilder.addNameOfInterface(translateQualifiedName(interfaceName));
        }

        // Translate method types
        typeBuilder.setMethods(new ArrayList<Method>());
        for (final Method method : type.getMethods()) {
            typeBuilder.addMethod(transform(method));
        }
        return new TypeMessage(typeBuilder.toType(), message.getContext());
    }

    private Method transform(final Method method) {
        final MethodBuilder mb = new MethodBuilder(method);
        mb.setReturnTypeName(translateQualifiedName(method.getReturnTypeName()));
        mb.setParameters(new ArrayList<Parameter>());
        for (final Parameter parameter : method.getParameters()) {
            mb.addParameter(new ParameterBuilder()
                    .setTypeName(translateQualifiedName(parameter.getTypeName()))
                    .setName(translateSimpleName(parameter.getName()))
                    .setAnnotations(parameter.getAnnotations())
                    .setFinal(parameter.isFinal())
                    .setComment(parameter.getComment())
                    .toParameter());
        }
        return mb.toMethod();
    }

    private Field transform(final Field field) {
        return new FieldBuilder().setName(translateSimpleName(field.getName())).setTypeName(translateQualifiedName(field.getTypeName())).setAnnotations(field.getAnnotations()).setModifiers(field.getModifiers()).setComment(field.getComment()).toField();
    }

    private SimpleName translateSimpleName(final SimpleName sourceName) {
        if (glossary.hasTranslation(sourceName, glossary.getSourceLanguage())) {
            final String translation = glossary.getTranslation(sourceName, glossary.getSourceLanguage());
            final boolean firstUpperCase = NameUtil.isFirstUpperCase(sourceName);
            return new SimpleName(NameUtil.toCamelHumpName(translation, firstUpperCase));
        } else {
            return sourceName;
        }
    }

    private QualifiedName translateQualifiedName(final QualifiedName sourceName) {
        final SimpleName simpleName = translateSimpleName(sourceName.getSimpleName());
        return QualifiedName.valueOf(sourceName.getPackage(), simpleName);
    }

    @Override
    public String toString() {
        return getName();
    }

}
