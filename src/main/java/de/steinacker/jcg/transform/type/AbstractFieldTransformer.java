/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.Field;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeBuilder;

import java.util.ArrayList;

/**
 * An abstract TypeTransformer that is used to replace all Fields with transformed Field instances.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public abstract class AbstractFieldTransformer extends AbstractTypeTransformer implements TypeTransformer {

    public final void visit(final Type type, final Context context) {
        // remove all existing Fields from the current TypeBuilder:
        getTypeBuilder(context).setFields(new ArrayList<Field>(type.getFields().size()));
        super.visit(type, context);
    }

    @Override
    public final void visit(final Field field, final Context context) {
        final TypeBuilder builder = context.getParameter(CTX_PARAM_TYPEBUILDER, TypeBuilder.class);
        builder.addField(transformField(field, context));
    }

    protected abstract Field transformField(final Field field, final Context context);
}
