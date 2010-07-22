/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.codegen.ProcessingContext;
import de.steinacker.jcg.codegen.TemplateProcessor;
import de.steinacker.jcg.model.*;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A ModelTransformer which adds constructors to a type.
 * <p>
 * By default, a single constructor with one parameter for every non-static field of the
 * type is generated.
 * <p>
 * If <code>onlyFinalFields</code> is set to <code>true</code>, the generated constructor
 * will only have parameters for final fields. This is useful to generate a constructor
 * for mutable objects. In this case, 'AddSetters' may be useful to add setters for the
 * non-final fields.
 * If all fields are non-final, a default constructor is generated if
 * <code>generateDefaultConstructor</code> is <code>true</code>.
 * <p>
 * If <code>generateDefaultConstructor</code> is set to <code>true</code>, a default
 * constructor is generated in addition to the constructor generated to initialize the
 * fields of the type.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddConstructors implements TypeTransformer {

    private String templateName;
    private TemplateProcessor processor;
    private boolean onlyFinalFields = false;
    private boolean finalAndNonFinalFields;
    private boolean generateDefaultConstructor = false;
    private String name;

    /**
     * Injects the name of the transformer.
     * @param name the name of the transformer.
     */
    @Required
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Injects the name of the template used to generate the code.
     * @param templateName the name of the template.
     */
    @Required
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    /**
     * Injects the TemplateProcessor used to generate the code.
     *
     * @param processor the TemplateProcessor
     */
    @Required
    public void setTemplateProcessor(final TemplateProcessor processor) {
        this.processor = processor;
    }

    /**
     * If <code>onlyFinalFields</code> is set to <code>true</code>, the generated constructor
     * will only have parameters for final fields. This is useful to generate a constructor
     * for mutable objects. In this case, 'AddSetters' may be useful to add setters for the
     * non-final fields.
     * <p>
     * If all fields are non-final, a default constructor is generated if
     * <code>generateDefaultConstructor</code> is <code>true</code>.
     *
     * @param onlyFinalFields specifies if only final fields will be taken into account. Default
     * is <code>false</code>
     */
    public void setOnlyFinalFields(final boolean onlyFinalFields) {
        this.onlyFinalFields = onlyFinalFields;
    }

    /**
     * If this is set to <code>true</code>, two constructors are generated, one for the
     * <code>final</code> fields, and a second for final and non-final fields. This
     * overrides the property <code>onlyFinalFields</code>.
     *
     * @param finalAndNonFinalFields
     */
    public void setFinalAndNonFinalFields(final boolean finalAndNonFinalFields) {
        this.finalAndNonFinalFields = finalAndNonFinalFields;
    }

    /**
     * If <code>generateDefaultConstructor</code> is set to <code>true</code>, a default
     * constructor is generated in addition to the constructor generated to initialize the
     * fields of the type.
     *
     * @param generateDefaultConstructor
     */
    public void setGenerateDefaultConstructor(final boolean generateDefaultConstructor) {
        this.generateDefaultConstructor = generateDefaultConstructor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<TypeMessage> transform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (type.getKind().equals(Type.Kind.CLASS)) {
            final List<Field> allNonStaticFields = new ArrayList<Field>();
            final List<Field> nonFinalFields = new ArrayList<Field>();
            final List<Field> finalFields = new ArrayList<Field>();
            for (final Field field : type.getFields()) {
                if (!field.is(FieldModifier.STATIC)) {
                    allNonStaticFields.add(field);
                    if (field.is(FieldModifier.FINAL))
                        finalFields.add(field);
                    else
                        nonFinalFields.add(field);
                }
            }
            final TypeBuilder typeBuilder = new TypeBuilder(type);
            if (finalAndNonFinalFields) {
                // generate a constructor with all non-static fields, one with all final fields, and
                // optionally a default constructors if there are no final fields an generateDefaultConstructor
                // is true
                if (allNonStaticFields.size() > 0) {
                    if (generateDefaultConstructor && allNonStaticFields.size() == nonFinalFields.size())
                        addMissingConstructor(typeBuilder, type, Collections.<Field>emptyList());
                    if (finalFields.size() > 0 && finalFields.size() < allNonStaticFields.size())
                        addMissingConstructor(typeBuilder, type, finalFields);
                    addMissingConstructor(typeBuilder, type, allNonStaticFields);
                } else {
                    if (generateDefaultConstructor)
                        addMissingConstructor(typeBuilder, type, Collections.<Field>emptyList());
                }
            } else if (onlyFinalFields) {
                // generate a constructor for the final fields and optionally add a default constructor,
                // if there are no final fields:
                if (generateDefaultConstructor && allNonStaticFields.size() == nonFinalFields.size())
                    addMissingConstructor(typeBuilder, type, Collections.<Field>emptyList());
                addMissingConstructor(typeBuilder, type, finalFields);
            } else {
                // generate a constructor with all non-static fields:
                if (generateDefaultConstructor && allNonStaticFields.size() == nonFinalFields.size())
                    addMissingConstructor(typeBuilder, type, Collections.<Field>emptyList());
                addMissingConstructor(typeBuilder, type, allNonStaticFields);
            }
            return Collections.singletonList(new TypeMessage(typeBuilder.toType(), message.getContext()));
        } else {
            return Collections.singletonList(message);
        }
    }

    private void addMissingConstructor(final TypeBuilder typeBuilder, final Type type, final List<Field> fields) {
        final ProcessingContext ctx = new ProcessingContext();
        final Method constructor = generateConstructor(ctx, type, fields);
        if (!TransformerUtil.hasMethodWithSignature(type, constructor)) {
            typeBuilder.addMethod(constructor);
            typeBuilder.addImports(ctx.getAddedImports());
        }

    }

    /**
     * Generates a Method based on a Type and a list of fields.
     * @param ctx the ProcessingContext used to gather the additional imports needed by the generated code.
     * @param type the Type
     * @param fields the list of fields
     * @return Method
     */
    private Method generateConstructor(final ProcessingContext ctx, final Type type, final List<Field> fields) {
        final MethodBuilder methodBuilder = new MethodBuilder()
                .setName(type.getName().getSimpleName())
                .setReturnType(null)
                .addModifier(MethodModifier.PUBLIC);
        // Wenn die Klasse final ist, müssen es die Methoden nicht sein:
        if (!type.getModifiers().contains(TypeModifier.FINAL)) {
            methodBuilder.addModifier(MethodModifier.FINAL);
        }
        final StringBuilder methodBody = new StringBuilder();
        for (final Field field : fields) {
            final Map<String, ?> arguments = Collections.singletonMap("field", field);
            processor.process(ctx, templateName, methodBody, arguments);
            methodBuilder
                    .addParameter(new ParameterBuilder()
                            .setFinal(true)
                            .setName(field.getName())
                            .setType(field.getType())
                            .toParameter());
            /*
            // TODO Kommentar nicht vergessen:
            commentBuilder.append(field.getComment());
            commentBuilder.append("\n").append("@return ").append(field.getName());

            mb.setComment(CodeUtil.toJavaDocComment(sb.toString()));
            */
        }
        /*
        // TODO Kommentar nicht vergessen:
        mb.setComment(CodeUtil.toJavaDocComment(sb.toString()));
         */
        methodBuilder.setMethodBody(methodBody.toString());
        return methodBuilder.toMethod();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
        /*
                .append("name", name)
                .append("generateDefaultConstructor", generateDefaultConstructor)
                .append("onlyFinalFields", onlyFinalFields)
                .append("finalAndNonFinalFields", finalAndNonFinalFields)
                .append("templateName", templateName.getName())
                .toString();
                */
    }

}