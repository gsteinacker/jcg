/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.codegen.ProcessingContext;
import de.steinacker.jcg.codegen.TemplateProcessor;
import de.steinacker.jcg.model.*;
import de.steinacker.jcg.util.NameUtil;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.Map;

/**
 * A TypeTransformer which adds getter methods for all non-static attributes of the different types.
 * <p/>
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddGetters extends AbstractFieldToMethodTransformer implements TypeTransformer {

    private String name;
    private String templateName;
    private TemplateProcessor processor;

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
     * Injects the name of the Transformer.
     *
     * @param name transformer name.
     */
    @Required
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected Method transformFieldToMethod(final Field field, final Context context) {
        // no getters for static fields:
        if (field.is(FieldModifier.STATIC))
            return null;

        final MethodBuilder mb = new MethodBuilder();
        // Alle Getter sind public:
        mb.addModifier(MethodModifier.PUBLIC);
        // Wenn die Klasse final ist, müssen es die Methoden nicht sein:
        if (!getType(context).getModifiers().contains(TypeModifier.FINAL))
            mb.addModifier(MethodModifier.FINAL);
        // Der Name der Methode:
        final String fieldName = field.getName().toString();
        mb.setName(SimpleName.valueOf("get" + NameUtil.toCamelHumpName(fieldName, true)));
        // Der Return-Type der Methode:
        mb.setReturnType(field.getType());
        final Map<String, ?> arguments = Collections.singletonMap("field", field);
        final StringBuilder methodBody = new StringBuilder();
        final ProcessingContext ctx = new ProcessingContext();
        processor.process(ctx, templateName, methodBody, arguments);
        mb.setMethodBody(methodBody.toString());
        getTypeBuilder(context).addImports(ctx.getAddedImports());
        /*
        // TODO Der Sourcecode:
        mb.setBody(CodeUtil.indent("return " + field.getName() + ";"));
        // TODO Kommentar nicht vergessen:
        final StringBuilder sb = new StringBuilder();
        sb.append(field.getComment());
        sb.append("\n").append("@return ").append(field.getName());
        mb.setComment(CodeUtil.toJavaDocComment(sb.toString()));
        */
        return mb.toMethod();
    }

    @Override
    public String toString() {
        return getName();
    }

 }