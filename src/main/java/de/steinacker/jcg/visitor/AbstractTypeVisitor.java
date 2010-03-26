/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.visitor;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.Field;
import de.steinacker.jcg.model.Method;
import de.steinacker.jcg.model.Type;

/**
 * An abstract visitor-implementation which is doing nothing but
 * traversing the type.
 * This class is only used for convenience purposes.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public abstract class AbstractTypeVisitor implements TypeVisitor {

    @Override
    public void visit(final Type type, final Context context) {
        for (final Method method : type.getMethods()) {
            visit(method, context);
        }

        for (final Field field : type.getFields()) {
            visit(field, context);
        }
    }

    @Override
    public void visit(final Method method, final Context context) {
    }

    @Override
    public void visit(final Field field, final Context context) {
    }
}