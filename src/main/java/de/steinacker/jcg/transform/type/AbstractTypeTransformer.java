/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.ContextBuilder;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeBuilder;
import de.steinacker.jcg.visitor.AbstractTypeVisitor;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public abstract class AbstractTypeTransformer extends AbstractTypeVisitor implements TypeTransformer {
    public static final String CTX_PARAM_TYPEBUILDER = "typeBuilder";
    public static final String CTX_PARAM_TYPE = "type";

    @Override
    public final TypeMessage transform(final TypeMessage message) {
        final Type type = message.getPayload();
        final Context ctx = createTransformerContext(message).toContext();
        visit(type, ctx);
        return new TypeMessage(getTypeBuilder(ctx).toType(), message.getContext());
    }

    /**
     * Creates a ContextBuilder for the current transformation.
     * This implementation adds a CTX_PARAM_TYPEBUILDER parameter, containing
     * a TypeBuilder instances that is initialized with the currently transformed type.
     * In addition to this, the current type is added as parameter CTX_PARAM_TYPE.
     * In order to add more parameters to the context, this method may be overridden.
     *
     * @param message the TypeMessage
     * @return ContextBuilder
     */
    protected ContextBuilder createTransformerContext(final TypeMessage message) {
        final TypeBuilder builder = new TypeBuilder(message.getPayload());
        return new ContextBuilder(message.getContext())
                .addParameter(CTX_PARAM_TYPEBUILDER, builder)
                .addParameter(CTX_PARAM_TYPE, message.getPayload());
    }

    protected final TypeBuilder getTypeBuilder(final Context context) {
        return context.getParameter(CTX_PARAM_TYPEBUILDER, TypeBuilder.class);
    }

    protected final Type getType(final Context context) {
        return context.getParameter(CTX_PARAM_TYPE, Type.class);
    }

}
