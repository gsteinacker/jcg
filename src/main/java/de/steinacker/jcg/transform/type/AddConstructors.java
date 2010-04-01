/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.ContextBuilder;
import de.steinacker.jcg.model.*;
import de.steinacker.jcg.util.DefaultFormatStringProvider;
import de.steinacker.jcg.util.FormatStringProvider;
import org.apache.log4j.Logger;

/**
 * A ModelTransformer which adds constructors to a type.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddConstructors extends AbstractTypeTransformer implements TypeTransformer {

    public final static String CTX_PARAM_METHODBUILDER = "methodBuilder";
    public final static String CTX_PARAM_METHODBODYBUILDER = "methodBodyBuilder";
    private final static Logger LOG = Logger.getLogger(AddConstructors.class);
    private FormatStringProvider formatStringProvider = new DefaultFormatStringProvider();

    /**
     * Inject a FormatStringProvider implementation used to generate method bodies for setters,
     * getters and constructors.
     *
     * @param provider the FormatStringProvider
     */
    public void setFormatStringProvider(final FormatStringProvider provider) {
        this.formatStringProvider = provider;
    }

    @Override
    public String getName() {
        return "AddConstructors";
    }

    @Override
    protected ContextBuilder createTransformerContext(TypeMessage message) {
        return super.createTransformerContext(message)
                .addParameter(CTX_PARAM_METHODBUILDER, new MethodBuilder())
                .addParameter(CTX_PARAM_METHODBODYBUILDER, new StringBuilder());
    }

    @Override
    public void visit(final Type type, final Context context) {
        final MethodBuilder methodBuilder = getMethodBuilder(context)
                .setName(type.getName().getSimpleName())
                .setReturnTypeName(null)
                .addModifier(MethodModifier.PUBLIC);
        // Wenn die Klasse final ist, müssen es die Methoden nicht sein:
        if (!type.getModifiers().contains(TypeModifier.FINAL)) {
            methodBuilder.addModifier(MethodModifier.FINAL);
        }
        super.visit(type, context);
        final String code = getMethodBodyBuilder(context).toString().trim();
        getTypeBuilder(context).addMethod(methodBuilder.setMethodBody(code).toMethod());
    }

    @Override
    public void visit(final Field field, final Context context) {
        if (!field.is(FieldModifier.STATIC)) {
            final Parameter p = new ParameterBuilder()
                    .setFinal(true)
                    .setName(field.getName())
                    .setTypeName(field.getTypeName())
                    .toParameter();
            getMethodBuilder(context).addParameter(p);
            // Method body:
            final String formatString = formatStringProvider.getFormatForSetter(field.getTypeName());
            final String code = String.format(formatString, field.getName(), field.getName());
            getMethodBodyBuilder(context).append(code).append("\n");

            /*
            // TODO Kommentar nicht vergessen:
            final StringBuilder sb = new StringBuilder();
            sb.append(field.getComment());
            sb.append("\n").append("@return ").append(field.getName());
            mb.setComment(CodeUtil.toJavaDocComment(sb.toString()));
            */
        }
    }

    protected final MethodBuilder getMethodBuilder(final Context context) {
        return context.getParameter(CTX_PARAM_METHODBUILDER, MethodBuilder.class);
    }

    protected final StringBuilder getMethodBodyBuilder(final Context context) {
        return context.getParameter(CTX_PARAM_METHODBODYBUILDER, StringBuilder.class);
    }

    @Override
    public String toString() {
        return getName();
    }

}