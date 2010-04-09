/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.*;
import de.steinacker.jcg.util.DefaultFormatStringProvider;
import de.steinacker.jcg.util.FormatStringProvider;
import de.steinacker.jcg.util.TransformerUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private final static Logger LOG = Logger.getLogger(AddConstructors.class);
    private FormatStringProvider formatStringProvider = new DefaultFormatStringProvider();
    private boolean onlyFinalFields = false;
    private boolean finalAndNonFinalFields;
    private boolean generateDefaultConstructor = false;
    private String name;

    /**
     * Injects the name of the transformer.
     * @param name
     */
    @Required
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Inject a {@link FormatStringProvider} implementation used to generate method bodies
     * for the constructors.
     * <p>
     * If no <code>FormatStringProvider</code> is injected, the
     * {@link DefaultFormatStringProvider} is used.
     *
     * @param provider the FormatStringProvider
     */
    public void setFormatStringProvider(final FormatStringProvider provider) {
        this.formatStringProvider = provider;
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
    public TypeMessage transform(final TypeMessage message) {
        final Type type = message.getPayload();
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
                if (finalFields.size() < allNonStaticFields.size())
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
        return new TypeMessage(typeBuilder.toType(), message.getContext());
    }

    private void addMissingConstructor(final TypeBuilder typeBuilder, final Type type, final List<Field> fields) {
        final Method constructor = generateConstructor(type, fields);
        if (!TransformerUtil.hasMethodWithSignature(type, constructor))
            typeBuilder.addMethod(constructor);
    }

    /**
     * Generates a Method based on a Type and a list of fields.
     * @param type the Type
     * @param fields the list of fields
     * @return Method
     */
    private Method generateConstructor(final Type type, final List<Field> fields) {
        final MethodBuilder methodBuilder = new MethodBuilder()
                .setName(type.getName().getSimpleName())
                .setReturnTypeName(null)
                .addModifier(MethodModifier.PUBLIC);
        // Wenn die Klasse final ist, müssen es die Methoden nicht sein:
        if (!type.getModifiers().contains(TypeModifier.FINAL)) {
            methodBuilder.addModifier(MethodModifier.FINAL);
        }
        final StringBuilder methodBody = new StringBuilder();
        for (final Field field : fields) {
            final String formatString = formatStringProvider.getFormatForSetter(field.getTypeName());
            final String code = String.format(formatString, field.getName(), field.getName());
            methodBuilder
                    .addParameter(new ParameterBuilder()
                            .setFinal(true)
                            .setName(field.getName())
                            .setTypeName(field.getTypeName())
                            .toParameter());
            // Method body:
            methodBody.append(code).append("\n");
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
        return name;
    }

}