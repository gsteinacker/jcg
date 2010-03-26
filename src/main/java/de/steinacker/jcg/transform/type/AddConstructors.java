/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.ContextBuilder;
import de.steinacker.jcg.model.*;
import de.steinacker.jcg.transform.predicate.Predicate;
import de.steinacker.jcg.transform.predicate.TruePredicate;
import org.apache.log4j.Logger;

/**
 * A ModelTransformer which adds constructors to a type.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddConstructors extends AbstractTypeTransformer implements TypeTransformer {

    public final static String CTX_PARAM_METHODBUILDER = "methodBuilder";
    private final static Logger LOG = Logger.getLogger(AddConstructors.class);
    private Predicate<Field> fieldPredicate = new TruePredicate<Field>();

    public final void setFieldPredicate(final Predicate<Field> fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }

    @Override
    public String getName() {
        return "AddConstructors";
    }

    @Override
    protected ContextBuilder createTransformerContext(TypeMessage message) {
        return super.createTransformerContext(message)
                .addParameter(CTX_PARAM_METHODBUILDER, new MethodBuilder());
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
        getTypeBuilder(context).addMethod(methodBuilder.toMethod());
    }

    @Override
    public void visit(final Field field, final Context context) {
        if (fieldPredicate == null || fieldPredicate.eval(field)) {
            final Parameter p = new ParameterBuilder()
                    .setFinal(true)
                    .setName(field.getName())
                    .setTypeName(field.getTypeName())
                    .toParameter();
            getMethodBuilder(context).addParameter(p);
            /*
            // TODO Der Sourcecode:
            mb.setBody(CodeUtil.indent("return " + field.getName() + ";"));
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

    @Override
    public String toString() {
        return getName();
    }

}